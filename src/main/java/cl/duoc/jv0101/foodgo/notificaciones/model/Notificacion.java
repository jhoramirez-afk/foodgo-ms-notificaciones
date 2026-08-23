package cl.duoc.jv0101.foodgo.notificaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;


@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El destinatario es obligatorio")
    @Column(nullable = false)
    private String destinatario;
    @Column
    private String canal;
    @Column
    private String mensaje;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDestinatario() { return destinatario; }

    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getCanal() { return canal; }

    public void setCanal(String canal) { this.canal = canal; }

    public String getMensaje() { return mensaje; }

    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

}
