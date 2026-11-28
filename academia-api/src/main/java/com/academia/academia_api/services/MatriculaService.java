package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.MatriculaCreateDTO;
import com.academia.academia_api.DTOs.MatriculaResponseDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.*;
import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.MatriculaMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PagamentoRepository;
import com.academia.academia_api.repository.PlanoRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class MatriculaService {

    private final MatriculaRepositoy matriculaRepositoy;
    private final PagamentoRepository pagamentoRepository;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final MatriculaMapper matriculaMapper;
    private final PagamentoService pagamentoService;

    public MatriculaService(MatriculaRepositoy matriculaRepositoy, PagamentoRepository pagamentoRepository, AlunoRepository alunoRepository,
                            PlanoRepository planoRepository, MatriculaMapper matriculaMapper, PagamentoService pagamentoService) {
        this.matriculaRepositoy = matriculaRepositoy;
        this.pagamentoRepository = pagamentoRepository;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
        this.matriculaMapper = matriculaMapper;
        this.pagamentoService = pagamentoService;
    }

    public PageResponseDTO<MatriculaResponseDTO> listarMatriculas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Matricula> matriculas = matriculaRepositoy.findAll(pageable);

        return new PageResponseDTO<>(
                matriculas.getContent().stream().map(matriculaMapper::toResponseDTO).toList(),
                matriculas.getNumber(),
                matriculas.getSize(),
                matriculas.getTotalElements(),
                matriculas.getTotalPages()
        );
    }

    public MatriculaResponseDTO getMeuPlano() {
        Usuarios usuario = getUsuarioLogado();

        Matricula matricula = matriculaRepositoy
                .findFirstByAlunoUsuarioIdOrderByMatriculaDesc(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum plano encontrado atrelado ao aluno."));

        MatriculaResponseDTO response = matriculaMapper.toResponseDTO(matricula);

        pagamentoRepository.findFirstByMatriculaMatriculaAndStatusOrderByDataCriacaoDesc(
                matricula.getMatricula(), StatusPagamentoEnum.PENDENTE
        ).ifPresent(p -> {
            response.setPagamentoId(p.getId());
            response.setPixCopiaECola(p.getCodigoPix());
            response.setQrCodeBase64(p.getQrCodeBase64());
        });

        return response;
    }

    public MatriculaResponseDTO findById(Long idMatricula) {
        if (idMatricula == null || idMatricula <= 0) {
            throw new BadRequestException("O ID da matrícula informado é inválido.");
        }

        Matricula matricula = matriculaRepositoy.findById(idMatricula)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada com o ID: " + idMatricula));

        return matriculaMapper.toResponseDTO(matricula);
    }

    public MatriculaResponseDTO criarMatricula(MatriculaCreateDTO dto) {
        Long idPlano = dto.getPlanoId();

        if (idPlano == null || idPlano <= 0) {
            throw new BadRequestException("O ID do plano deve ser válido e maior que zero.");
        }

        Aluno aluno;

        if (dto.getAlunoId() != null && dto.getAlunoId() > 0) {
            aluno = alunoRepository.findById(dto.getAlunoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com o ID: " + dto.getAlunoId()));
        } else {
            Usuarios usuarioLogado = getUsuarioLogado();
            aluno = alunoRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado para o usuário logado."));
        }

        var matriculaAtivaExistente = matriculaRepositoy.findByAlunoIdAndAtiva(aluno.getId(), true);
        if (matriculaAtivaExistente != null) {
            throw new BadRequestException("O aluno informado já possui uma matrícula ativa.");
        }

        Plano plano = planoRepository.findById(idPlano)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com o ID: " + idPlano));

        Matricula novaMatricula = matriculaMapper.toEntity(dto);
        novaMatricula.setAluno(aluno);
        novaMatricula.setPlano(plano);
        novaMatricula.setAtiva(false);
        novaMatricula.setStatus(MatriculaStatusEnum.AGUARDANDO_PRIMEIRO_PAGAMENTO);

        Matricula salva = matriculaRepositoy.save(novaMatricula);

        Pagamento pagamento = pagamentoService.criarPagamentoPix(salva);

        MatriculaResponseDTO response = matriculaMapper.toResponseDTO(salva);
        response.setPagamentoId(pagamento.getId());
        response.setPixCopiaECola(pagamento.getCodigoPix());
        response.setQrCodeBase64(pagamento.getQrCodeBase64());

        return response;
    }

    @Transactional
    public MatriculaResponseDTO agendarTrocaPlano(Long idMatricula, Long idPlano) {
        Matricula m = matriculaRepositoy.findById(idMatricula)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada com o ID: " + idMatricula));

        if (m.getStatus() != MatriculaStatusEnum.ATIVA_PAGO) {
            throw new BadRequestException("A troca de plano só é permitida com a mensalidade em dia.");
        }
        Plano plano = planoRepository.findById(idPlano)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com o ID: " + idPlano));

        m.setPlanoAgendado(plano);
        return matriculaMapper.toResponseDTO(matriculaRepositoy.save(m));
    }

    @Transactional
    public MatriculaResponseDTO cancelarAssinatura(Long idMatricula) {
        Matricula m = matriculaRepositoy.findById(idMatricula)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada com o ID: " + idMatricula));

        if (m.getStatus() != MatriculaStatusEnum.ATIVA_PAGO) {
            throw new BadRequestException("Só é possível cancelar com a mensalidade em dia.");
        }
        m.setPlanoAgendado(null);
        m.setStatus(MatriculaStatusEnum.ATIVA_CANCELAMENTO_AGENDADO);
        return matriculaMapper.toResponseDTO(matriculaRepositoy.save(m));
    }

    @Transactional
    public MatriculaResponseDTO reativar(Long idMatricula) {
        Matricula m = matriculaRepositoy.findById(idMatricula)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada com o ID: " + idMatricula));

        if (m.getStatus() != MatriculaStatusEnum.INATIVA) {
            throw new BadRequestException("Apenas matrículas inativas podem ser reativadas.");
        }
        m.setStatus(MatriculaStatusEnum.INATIVA_PENDENTE_PAGAMENTO);
        matriculaRepositoy.save(m);

        Pagamento p = pagamentoService.criarPagamentoPix(m);
        MatriculaResponseDTO response = matriculaMapper.toResponseDTO(m);
        response.setPagamentoId(p.getId());
        response.setPixCopiaECola(p.getCodigoPix());
        response.setQrCodeBase64(p.getQrCodeBase64());
        return response;
    }

    private static final Set<Integer> DIAS_PERMITIDOS = Set.of(1, 5, 10, 15, 20, 25);

    @Transactional
    public MatriculaResponseDTO definirDiaVencimento(Long idMatricula, Integer dia) {
        if (dia == null || !DIAS_PERMITIDOS.contains(dia)) {
            throw new BadRequestException("Dia de vencimento inválido. Opções: " + DIAS_PERMITIDOS);
        }

        Matricula m = matriculaRepositoy.findById(idMatricula)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada com o ID: " + idMatricula));

        if (m.getStatus() != MatriculaStatusEnum.ATIVA_PAGO) {
            throw new BadRequestException("O dia de vencimento só pode ser escolhido após o primeiro pagamento.");
        }
        if (m.getDiaVencimento() != null) {
            throw new BadRequestException("O dia de vencimento já foi definido.");
        }

        LocalDate base = LocalDate.now().plusMonths(m.getPlano().getTipo().meses());
        LocalDate primeiroVencimento = base.withDayOfMonth(dia);
        if (primeiroVencimento.isBefore(base)) {
            primeiroVencimento = primeiroVencimento.plusMonths(1);
        }

        m.setDiaVencimento(dia);
        m.setDataVencimento(primeiroVencimento);
        return matriculaMapper.toResponseDTO(matriculaRepositoy.save(m));
    }

    public MatriculaResponseDTO alterarStatus(Long idMatricula, boolean novoStatus) {
        if (idMatricula == null || idMatricula <= 0) {
            throw new BadRequestException("O ID da matrícula informado é inválido.");
        }

        var buscaMatricula = matriculaRepositoy.findByMatricula(idMatricula, novoStatus);
        if (buscaMatricula == null || novoStatus == buscaMatricula.isAtiva() ) {
            throw new ResourceNotFoundException("Matrícula não encontrada ou já possui o status selecionado.");
        }

        buscaMatricula.setAtiva(novoStatus);
        Matricula matriculaAtualizada = matriculaRepositoy.save(buscaMatricula);

        return matriculaMapper.toResponseDTO(matriculaAtualizada);
    }

    private Usuarios getUsuarioLogado() {
        return (Usuarios) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}