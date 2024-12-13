package at.technikumwien.SWKOM2024.repositories;

import at.technikumwien.SWKOM2024.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
}