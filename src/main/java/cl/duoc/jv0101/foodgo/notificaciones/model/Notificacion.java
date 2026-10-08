package cl.duoc.jv0101.foodgo.notificaciones.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
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

    @Valid
    @OneToMany(mappedBy = "notificacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("notificacion-intentos")
    private List<IntentoEnvio> intentos = new ArrayList<>();

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDestinatario() { return destinatario; }

    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getCanal() { return canal; }

    public void setCanal(String canal) { this.canal = canal; }

    public String getMensaje() { return mensaje; }

    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public List<IntentoEnvio> getIntentos() {
        return intentos;
    }

    public void setIntentos(List<IntentoEnvio> items) {
        this.intentos.clear();
        if (items != null) {
            items.forEach(this::addIntentoEnvio);
        }
    }

    public void addIntentoEnvio(IntentoEnvio item) {
        intentos.add(item);
        item.setNotificacion(this);
    }

    public void removeIntentoEnvio(IntentoEnvio item) {
        intentos.remove(item);
        item.setNotificacion(null);
    }
}
