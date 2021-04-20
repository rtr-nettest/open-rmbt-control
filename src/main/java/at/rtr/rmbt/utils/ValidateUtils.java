package at.rtr.rmbt.utils;

import at.rtr.rmbt.exception.EmptyClientVersionException;
import com.vdurmont.semver4j.Requirement;
import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.SemverException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidateUtils {

    public void validateClientVersion(String configVersion, String clientVersion) {
        if(clientVersion.isEmpty() || clientVersion.isBlank()){
            throw new EmptyClientVersionException();
        }
        String versionString = clientVersion.length() == 3 ? clientVersion + ".0" : clientVersion;
        Semver semverVersion = new Semver(versionString, Semver.SemverType.NPM);
        Requirement requirement = Requirement.buildNPM(configVersion);

        if (!semverVersion.satisfies(requirement)) {
            throw new SemverException("Requirement not satisfied");
        }
    }
}
