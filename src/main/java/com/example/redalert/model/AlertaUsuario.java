package com.example.redalert.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ALERTAS_USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_alertas_usuario_generator")
    @SequenceGenerator(name = "seq_alertas_usuario_generator", sequenceName = "SEQ_ALERTAS_USUARIO", allocationSize = 1)
    @Column(name = "id_alerta")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_reportou", nullable = false)
    private Usuario usuarioReportou;

    @NotBlank
    @Size(max = 2000)
    @Column(name = "descricao_texto", nullable = false, length = 2000)
    private String descricaoTexto;

    @NotNull
    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @NotNull
    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "timestamp_reporte", nullable = false)
    private Instant timestampReporte;

    @NotBlank
    @Column(name = "status_alerta", nullable = false, length = 30)
    private String statusAlerta;

    @Column(name = "severidade_ia", length = 20, nullable = true)
    private String severidadeIA;

    @Column(name = "tipo_ia", length = 30, nullable = true)
    private String tipoIA;

    // @Column(name = "id_hotspot_associado", nullable=true)
    // private Long idHotspotAssociado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hotspot_associado", nullable = true)
    private HotspotsEventos hotspotAssociado;

}
