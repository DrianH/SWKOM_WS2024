package at.technikumwien.SWKOM2024.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Document name cannot be empty")
    @Size(max = 255, message = "Document name must be less than 255 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Document type cannot be empty")
    @Size(max = 50, message = "Document type must be less than 50 characters")
    @Column(nullable = false)
    private String type;

    @NotNull(message = "Document size must be provided")
    @Min(value = 1, message = "Document size must be greater than zero")
    @Column(nullable = false)
    private Long size;

    @Lob
    @NotNull(message = "Document content must not be null")
    @Column(nullable = true)
    private byte[] content;  // This will store the file data as a BLOB
}