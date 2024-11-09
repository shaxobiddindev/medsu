package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
