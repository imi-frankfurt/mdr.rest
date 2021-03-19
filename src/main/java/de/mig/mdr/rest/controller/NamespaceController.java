package de.mig.mdr.rest.controller;

import de.mig.mdr.rest.MdrRestApplication;
import de.mig.mdr.dal.jooq.enums.Status;
import de.mig.mdr.dal.jooq.tables.pojos.ScopedIdentifier;
import de.mig.mdr.model.Deserializer;
import de.mig.mdr.model.dto.element.Element;
import de.mig.mdr.model.dto.element.Namespace;
import de.mig.mdr.model.dto.element.section.Identification;
import de.mig.mdr.model.service.ElementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/namespaces")
public class NamespaceController {

  private ElementService elementService;

  @Autowired
  public NamespaceController(ElementService elementService) {
    this.elementService = elementService;
  }

  /**
   * TODO.
   */
  @GetMapping("/writable")
  @Operation(summary = " ", //TODO
      description = " ",
      tags = {"Namespace"},
      security = @SecurityRequirement(name = "basicAuth"))
  public List<Namespace> getWritable() {
    /*try (DSLContext ctx = ResourceManager.getDslContext()) {
      MdrUser user = MdrRestApplication.getCurrentUser();
      return NamespaceHandler.getWritable(ctx, user.getId());
    }*/
    return new ArrayList<>(); // TODO remove
  }

  /**
   * Create a new Namespace and return its new ID.
   */
  @PostMapping
  @Operation(summary = " ", //TODO
      description = " ",
      tags = {"Namespace"},
      security = @SecurityRequirement(name = "basicAuth"))
  public ResponseEntity create(@RequestBody String content,
      UriComponentsBuilder uriComponentsBuilder) {
    Element element = Deserializer.getElement(content);
    Integer userId = MdrRestApplication.getCurrentUser().getId();
    try {
      ScopedIdentifier scopedIdentifier = elementService.create(userId, element);
      UriComponents uriComponents =
          uriComponentsBuilder.path("/namespaces/{id}")
              .buildAndExpand(scopedIdentifier.getIdentifier());
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(uriComponents.toUri());
      return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    } catch (IllegalAccessException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /**
   * Read a namespace by its id.
   */
  @GetMapping("/{namespaceId}")
  @Operation(summary = "Get Namespace by id.",
      description = " ",//TODO
      tags = {"Namespace"},
      security = @SecurityRequirement(name = "basicAuth"))
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public ResponseEntity read(@PathVariable(value = "namespaceId") String namespaceId) {
    try {
      Element element = elementService
          .read(MdrRestApplication.getCurrentUser().getId(), namespaceId);
      return new ResponseEntity<>(element, HttpStatus.OK);
    } catch (NoSuchElementException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Updates an existing Namespace.
   */
  @PutMapping("/{namespaceId}")
  @Operation(summary = " ", //TODO
      description = " ", //TODO
      tags = {"Namespace"},
      security = @SecurityRequirement(name = "basicAuth"))
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public ResponseEntity update(@PathVariable(value = "namespaceId") String oldNamespaceId,
      @RequestBody String content, UriComponentsBuilder uriComponentsBuilder) {
    Element element = Deserializer.getElement(content);
    Integer userId = MdrRestApplication.getCurrentUser().getId();
    Element oldNamespace = elementService
        .read(MdrRestApplication.getCurrentUser().getId(), oldNamespaceId);

    if (oldNamespace == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (oldNamespace.getIdentification().getStatus() == Status.RELEASED && (
        element.getIdentification().getStatus() == Status.STAGED
            || element.getIdentification().getStatus() == Status.DRAFT)) {
      return new ResponseEntity<>("Status change from released to draft or staged not allowed.",
          HttpStatus.BAD_REQUEST);
    }

    element.setIdentification(oldNamespace.getIdentification());
    try {
      Identification identification = elementService.update(userId, element);
      UriComponents uriComponents =
          uriComponentsBuilder.path("/namespaces/{id}")
              .buildAndExpand(identification.getIdentifier());
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(uriComponents.toUri());
      return new ResponseEntity<>(httpHeaders, HttpStatus.NO_CONTENT);
    } catch (IllegalAccessException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (UnsupportedOperationException | IllegalArgumentException e) {
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
  }

  /**
   * Delete a namespace.
   * Only drafts are really deleted. Released namespaces are marked as outdated.
   * @return
   */
  @DeleteMapping("/{namespaceId}")
  @Operation(summary = "Delete Namespace by id.",
      description = " ", //TODO
      tags = {"Namespace"},
      security = @SecurityRequirement(name = "basicAuth"))
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public ResponseEntity delete(@PathVariable(value = "namespaceId") String namespaceId) {
    try {
      Element namespace = elementService
          .read(MdrRestApplication.getCurrentUser().getId(), namespaceId);
      elementService.delete(MdrRestApplication.getCurrentUser().getId(),
          namespace.getIdentification().getUrn());
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (NoSuchElementException nse) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Release a namespace.
   */
  @PatchMapping("/{namespaceId}/release")
  @Operation(summary = "Release Namespace by id.",
      description = " ", //TODO
      tags = {"Namespace"},
      security = @SecurityRequirement(name = "basicAuth"))
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public ResponseEntity release(@PathVariable(value = "namespaceId") String namespaceId) {
    try {
      Element namespace = elementService
          .read(MdrRestApplication.getCurrentUser().getId(), namespaceId);
      elementService.release(MdrRestApplication.getCurrentUser().getId(),
          namespace.getIdentification().getUrn());
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (IllegalArgumentException | IllegalStateException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (NoSuchElementException nse) {
      return new ResponseEntity<>(nse.getMessage(), HttpStatus.NOT_FOUND);
    }
  }

}
