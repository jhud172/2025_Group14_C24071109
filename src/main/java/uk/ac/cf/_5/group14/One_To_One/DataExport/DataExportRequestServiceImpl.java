package uk.ac.cf._5.group14.One_To_One.DataExport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

@Service
public class DataExportRequestServiceImpl implements DataExportRequestService {

    private final DataExportRequestRepository repository;

    public DataExportRequestServiceImpl(DataExportRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public DataExportRequest createRequest(User user) {
        if (user == null || user.getId() == null) {
            return null;
        }
        DataExportRequest request = new DataExportRequest();
        request.setUser(user);
        request.setStatus(DataExportStatus.REQUESTED);
        return repository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataExportRequest> getRecentRequests(User user) {
        if (user == null || user.getId() == null) {
            return List.of();
        }
        return repository.findTop5ByUserIdOrderByRequestedAtDesc(user.getId());
    }
}
