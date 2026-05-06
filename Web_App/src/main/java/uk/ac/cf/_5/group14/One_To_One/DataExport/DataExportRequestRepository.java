package uk.ac.cf._5.group14.One_To_One.DataExport;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataExportRequestRepository extends JpaRepository<DataExportRequest, Long> {
    List<DataExportRequest> findTop5ByUserIdOrderByRequestedAtDesc(Long userId);
}
