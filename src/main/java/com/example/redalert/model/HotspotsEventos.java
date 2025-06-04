package com.example.redalert.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HOTSPOTS_EVENTOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotspotsEventos {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_hotspots_eventos_generator")
    @SequenceGenerator(name = "seq_hotspots_eventos_generator", sequenceName = "SEQ_HOTSPOTS_EVENTOS", allocationSize = 1)
    @Column(name = "id_hotspot")
    private Long id;

    @NotNull
    @Column(name = "centroide_latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal centroideLatitude;

    @NotNull
    @Column(name = "centroide_longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal centroideLongitude;

    @Column(name = "raio_estimado_km", precision = 6, scale = 2, nullable = true)
    private BigDecimal raioEstimadoKm;

    @Lob
    @Column(name = "poligono_geojson", nullable = true, columnDefinition = "CLOB")
    private String poligonoGeojson;

    @NotNull
    @Column(name = "numero_alertas_agrupados", nullable = false)
    private Integer numeroAlertasAgrupados = 0;

    @NotBlank
    @Column(name = "severidade_predominante", nullable = false, length = 20)
    private String severidadePredominante;

    @NotBlank
    @Column(name = "tipo_predominante", nullable = false, length = 30)
    private String tipoPredominante;

    @Column(name = "resumo_publico", length = 500, nullable = true)
    private String resumoPublico;

    @CreationTimestamp
    @Column(name = "timestamp_criacao", nullable = false, updatable = false)
    private Instant timestampCriacao;

    @UpdateTimestamp
    @Column(name = "timestamp_ultima_atividade", nullable = false)
    private Instant timestampUltimaAtividade;

    @NotBlank
    @Column(name = "status_hotspot", nullable = false, length = 20)
    private String statusHotspot;

    @OneToMany(mappedBy = "hotspotAssociado", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AlertaUsuario> alertasAgrupados = new ArrayList<>();

    public void adicionarAlerta(AlertaUsuario alerta) {
        alertasAgrupados.add(alerta);
        alerta.setHotspotAssociado(this);
    }

    public void removerAlerta(AlertaUsuario alerta) {
        alertasAgrupados.remove(alerta);
        alerta.setHotspotAssociado(null);
    }
}
