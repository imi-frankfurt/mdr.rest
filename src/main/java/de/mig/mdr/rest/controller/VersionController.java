package de.mig.mdr.rest.controller;

import de.mig.mdr.model.dto.BuildInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionController {

  @Value("${git.build.version}")
  private String buildVersion;

  @Value("${git.build.time}")
  private String buildDate;

  @Value("${git.branch}")
  private String buildBranch;

  @Value("${git.commit.id.abbrev}")
  private String buildHash;

  /**
   * Returns the build information for the running REST instance.
   */
  @GetMapping()
  public BuildInformation getVersion() {
    BuildInformation buildInformation = new BuildInformation();
    buildInformation.setBuildBranch(buildBranch);
    buildInformation.setBuildDate(buildDate);
    buildInformation.setBuildHash(buildHash);
    buildInformation.setBuildVersion(buildVersion);
    return buildInformation;
  }

}
