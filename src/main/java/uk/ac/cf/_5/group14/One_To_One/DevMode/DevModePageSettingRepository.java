package uk.ac.cf._5.group14.One_To_One.DevMode;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevModePageSettingRepository extends JpaRepository<DevModePageSetting, Long> {
    Optional<DevModePageSetting> findByPageKey(String pageKey);
}
