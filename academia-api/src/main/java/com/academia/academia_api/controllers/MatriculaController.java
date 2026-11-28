package com.academia.academia_api.controllers;
import com.academia.academia_api.DTOs.*;
import com.academia.academia_api.services.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Matriculas.",
        description = "Gerenciamento das Matriculas da academia."
)
@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @Operation(
            summary = "Listar todas as Matriculas.",
            description = "Lista todas as Matriculas com paginação, filtros e ordenação."
    )
   @GetMapping
    public ResponseEntity<PageResponseDTO<MatriculaResponseDTO>> matriculas(
           @RequestParam(defaultValue = "0")
           int page,

           @RequestParam(defaultValue = "10")
           int size
   ) {
        return ResponseEntity.ok(matriculaService.listarMatriculas(page, size));
   }

    @Operation(
            summary = "Busca plano do aluno logado."
    )
    @GetMapping("/me")
    public ResponseEntity<MatriculaResponseDTO> meuPlano() {

        return ResponseEntity.ok(
                matriculaService.getMeuPlano()
        );

    }

    @Operation(
            summary = "Buscar Matriculas por Id."
    )
    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponseDTO> getMatricula(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.findById(id));
    }

    @Operation(
            summary = "Cria uma nova Matricula."
    )
    @PostMapping
    public ResponseEntity<MatriculaResponseDTO> criarMatricula(
            @Valid @RequestBody MatriculaCreateDTO dto){

        MatriculaResponseDTO novaMatricula = matriculaService.criarMatricula(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novaMatricula);
    }

    @Operation(
            summary = "Editar o plano de uma Matricula."
    )
    @PutMapping("/{idMatricula}/plano/{idPlano}")
    public ResponseEntity<MatriculaResponseDTO> editarPlano(@PathVariable Long idMatricula, @PathVariable Long idPlano) {

        MatriculaResponseDTO response = matriculaService.agendarTrocaPlano(idMatricula, idPlano);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Desativa uma Matricula ativa por Id."
    )
    @PatchMapping("/{id}/ativo")
    public ResponseEntity<MatriculaResponseDTO> alterarStatus(@PathVariable Long id, @RequestParam boolean novoStatus){
        MatriculaResponseDTO matriculaDesativada = matriculaService.alterarStatus(id, novoStatus);
        return ResponseEntity.ok(matriculaDesativada);
    }

    @Operation(summary = "Define o dia de vencimento (obrigatório após o 1º pagamento).")
    @PatchMapping("/{id}/dia-vencimento")
    public ResponseEntity<MatriculaResponseDTO> definirDiaVencimento(
            @PathVariable Long id, @Valid @RequestBody DiaVencimentoDTO dto) {
        return ResponseEntity.ok(matriculaService.definirDiaVencimento(id, dto.diaVencimento()));
    }

    @Operation(summary = "Cancela a assinatura: mantém o acesso até o próximo vencimento.")
    @PostMapping("/{id}/cancelamento")
    public ResponseEntity<MatriculaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.cancelarAssinatura(id));
    }

    @Operation(summary = "Reativa uma matrícula inativa e gera novo Pix.")
    @PostMapping("/{id}/reativacao")
    public ResponseEntity<MatriculaResponseDTO> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.reativar(id));
    }
}
