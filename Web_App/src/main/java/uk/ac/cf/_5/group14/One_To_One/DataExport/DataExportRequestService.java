package uk.ac.cf._5.group14.One_To_One.DataExport;

import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

public interface DataExportRequestService {
    DataExportRequest createRequest(User user);
    List<DataExportRequest> getRecentRequests(User user);
}
