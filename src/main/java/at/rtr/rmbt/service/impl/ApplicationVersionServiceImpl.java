package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.model.Settings;
import at.rtr.rmbt.repository.SettingsRepository;
import at.rtr.rmbt.response.ApplicationVersionResponse;
import at.rtr.rmbt.service.ApplicationVersionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationVersionServiceImpl implements ApplicationVersionService {

    @Value("${git.branch}")
    private String branch;

    @Value("${git.commit.id.describe}")
    private String describe;

    @Value("${application-version.host}")
    private String applicationHost;

    private final SettingsRepository settingsRepository;

    @Override
    public ApplicationVersionResponse getApplicationVersion() {
        return ApplicationVersionResponse.builder()
                .version(String.format(Constants.VERSION_TEMPLATE, branch, describe))
                .systemUUID(getSystemUUID())
                .host(applicationHost)
                .build();
    }

    private String getSystemUUID() {
        return settingsRepository.findFirstByKeyAndLangIsNullOrKeyAndLangOrderByLang(Config.SYSTEM_UUID_KEY, Config.SYSTEM_UUID_KEY, StringUtils.EMPTY)
                .map(Settings::getValue)
                .orElse(null);
    }
}
