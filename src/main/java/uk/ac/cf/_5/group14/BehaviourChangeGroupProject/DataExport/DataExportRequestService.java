package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DataExport;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface DataExportRequestService {
    DataExportRequest createRequest(User user);
    List<DataExportRequest> getRecentRequests(User user);
}
