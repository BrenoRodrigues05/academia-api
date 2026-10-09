package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.MatriculaCreateDTO;
import com.academia.academia_api.DTOs.MatriculaResponseDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.*;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.MatriculaMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PagamentoRepository;
import com.academia.academia_api.repository.PlanoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    public MatriculaResponseDTO getMeuPlano(){
        Usuarios usuarios = getUsuarioLogado();

        Matricula matricula = matriculaRepositoy.findByAlunoUsuarioIdAndAtivaTrue(usuarios.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Nenhum plano encontrado atrelado ao aluno."));

        MatriculaResponseDTO response = matriculaMapper.toResponseDTO(matricula);

        if (!matricula.isAtiva()) {
            pagamentoRepository.findFirstByMatriculaMatriculaAndStatusOrderByMatriculaDesc(
                    matricula.getMatricula(),
                    StatusPagamentoEnum.PENDENTE
            ).ifPresent(pagamento -> {
                response.setPagamentoId(getMeuPlano().getPagamentoId());
                response.setPixCopiaECola(getMeuPlano().getPixCopiaECola());
                response.setQrCodeBase64(getMeuPlano().getQrCodeBase64());
            });
        }

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

        Matricula salva = matriculaRepositoy.save(novaMatricula);

        Pagamento pagamento = pagamentoService.criarPagamentoPix(salva);

        MatriculaResponseDTO response = matriculaMapper.toResponseDTO(salva);
        response.setPagamentoId(pagamento.getId());
        response.setPixCopiaECola(pagamento.getCodigoPix());
        response.setQrCodeBase64(pagamento.getQrCodeBase64());

        return response;
    }

    public  MatriculaResponseDTO editarPlanoMatricula(Long idMatricula, Long idPlano){
        if (idMatricula == null || idMatricula <= 0) {
            throw new BadRequestException("O ID da matrícula informado é inválido.");
        }

        Matricula matricula = matriculaRepositoy.findById(idMatricula)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada com o ID: " + idMatricula));

        Plano plano = planoRepository.findById(idPlano)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com o ID: " + idPlano));

        matricula.setPlano(plano);
        matriculaRepositoy.save(matricula);

        return matriculaMapper.toResponseDTO(matricula);
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