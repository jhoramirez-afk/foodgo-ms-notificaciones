package cl.duoc.jv0101.foodgo.notificaciones.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "intentos_envio")
public class IntentoEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Resultado es obligatorio")
    @Pattern(regexp = "ENVIADO|FALLIDO", message = "Resultado debe ser ENVIADO, FALLIDO")
    @Column(nullable = false)
    private String resultado;

    @NotNull(message = "Fecha del intento es obligatoria")
    @PastOrPresent(message = "La fecha no puede estar en el futuro")
    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @NotBlank(message = "Detalle del intento es obligatorio")
    @Size(max = 255, message = "El campo admite hasta 255 caracteres")
    @Column(nullable = false)
    private String detalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notificacion_id", nullable = false)
    @JsonBackReference("notificacion-intentos")
    private Notificacion notificacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }
}
