package de.mig.mdr.rest.controller;

import de.mig.mdr.rest.MdrRestApplication;
import de.mig.mdr.dal.jooq.enums.Status;
import de.mig.mdr.dal.jooq.tables.pojos.ScopedIdentifier;
import de.mig.mdr.model.Deserializer;
import de.mig.mdr.model.dto.element.DataElement;
import de.mig.mdr.model.dto.element.DataElementGroup;
import de.mig.mdr.model.dto.element.Element;
import de.mig.mdr.model.dto.element.Record;
import de.mig.mdr.model.dto.element.section.Identification;
import de.mig.mdr.model.handler.element.section.IdentificationHandler;
import de.mig.mdr.model.service.ElementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/element")
@Tag(name = "Element", description = "")
public class ElementController {

  private ElementService elementService;

  @Autowired
  public ElementController(ElementService elementService) {
    this.elementService = elementService;
  }

  /**
   * Create a new Element and return its new ID.
   */
  @PostMapping
  @Operation(summary = "Create an Element with one of the models Dataelement, Dataelementgroup "
      + "or Record",
      description = "",
      tags = {"Element"},
      security = @SecurityRequirement(name = "basicAuth"))
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "",
      content =
      @Content(
          schema = @Schema(oneOf = {DataElement.class, DataElementGroup.class, Record.class}),
          examples = {
              @ExampleObject(name = "DATAELEMENT", value = "{\n"
                  + "    \"identification\": {\n"
                  + "    \"elementType\": \"DATAELEMENT\",\n"
                  + "    \"namespaceId\": 1,\n"
                  + "    \"status\": \"RELEASED\"\n"
                  + "  },\n"
                  + "  \"definitions\": [\n"
                  + "    {\n"
                  + "      \"designation\": \"Geschlecht\",\n"
                  + "      \"definition\": \"Geschlecht des Patienten\",\n"
                  + "      \"language\": \"de\"\n"
                  + "    },\n"
                  + "    {\n"
                  + "      \"designation\": \"gender\",\n"
                  + "      \"definition\": \"patient´s gender\",\n"
                  + "      \"language\": \"en\"\n"
                  + "    }\n"
                  + "  ],\n"
                  + "  \"validation\": {\n"
                  + "    \"type\": \"STRING\",\n"
                  + "        \"text\": \n"
                  + "            {\n"
                  + "            \"useRegEx\": false,\n"
                  + "            \"regEx\": null,\n"
                  + "            \"useMaximumLength\": false\n"
                  + "            }\n"
                  + "\n"
                  + "    },\n"
                  + "  \"slots\": [\n"
                  + "    {\n"
                  + "      \"name\": \"slot_name\",\n"
                  + "      \"value\": \"slot_value\"\n"
                  + "    }\n"
                  + "  ],\n"
                  + "  \"conceptAssociations\": [\n"
                  + "    {\n"
                  + "      \"conceptId\": 1,\n"
                  + "      \"system\": \"ca_system\",\n"
                  + "      \"sourceId\": 1,\n"
                  + "      \"version\": \"ca_version\",\n"
                  + "      \"term\": \"ca_term\",\n"
                  + "      \"text\": \"ca_text\",\n"
                  + "      \"linktype\": \"undefined\",\n"
                  + "      \"scopedIdentifierId\": 1\n"
                  + "    }\n"
                  + "  ]\n"
                  + "}\n"),
              @ExampleObject(name = "DATAELEMENTGROUP", value = "{\n"
                  + "    \"identification\": {\n"
                  + "    \"elementType\": \"DATAELEMENTGROUP\",\n"
                  + "    \"namespaceId\": 1,\n"
                  + "    \"status\": \"RELEASED\"\n"
                  + "  },\n"
                  + "  \"definitions\": [\n"
                  + "    {\n"
                  + "      \"designation\": \"people\",\n"
                  + "      \"definition\": \"smartphone\",\n"
                  + "      \"language\": \"de\"\n"
                  + "    }\n"
                  + "    ],\n"
                  + "  \"members\": \n"
                  + "  {\n"
                  + "    \"dataElementGroups\": [\n"
                  + "        \n"
                  + "      ],\n"
                  + "   \"dataElements\": [\n"
                  + "      \"urn:1:dataelement:1:1\"\n"
                  + "  ],\n"
                  + "  \"records\": [\n"
                  + "      \n"
                  + "    ]\n"
                  + "  }\n"
                  + "  ,\n"
                  + "  \"slots\": [\n"
                  + "    {\n"
                  + "      \"name\": \"slot_DEG\",\n"
                  + "      \"value\": \"slot_value\"\n"
                  + "    }\n"
                  + "  ]\n"
                  + "}"),
              @ExampleObject(name = "RECORD", value = "{\n"
                  + "    \"identification\": {\n"
                  + "    \"elementType\": \"RECORD\",\n"
                  + "    \"namespaceId\": 1,\n"
                  + "    \"status\": \"RELEASED\"\n"
                  + "  },\n"
                  + "  \"definitions\": [\n"
                  + "    {\n"
                  + "      \"designation\": \"people\",\n"
                  + "      \"definition\": \"smartphone\",\n"
                  + "      \"language\": \"de\"\n"
                  + "    }\n"
                  + "    ],\n"
                  + "  \"members\": \n"
                  + "  {\n"
                  + "    \"dataElementGroups\": [\n"
                  + "        \n"
                  + "      ],\n"
                  + "   \"dataElements\": [\n"
                  + "      \"urn:1:dataelement:1:1\"\n"
                  + "  ],\n"
                  + "  \"records\": [\n"
                  + "      \n"
                  + "    ]\n"
                  + "  }\n"
                  + "  ,\n"
                  + "  \"slots\": [\n"
                  + "    {\n"
                  + "      \"name\": \"slot_DEG\",\n"
                  + "      \"value\": \"slot_value\"\n"
                  + "    }\n"
                  + "  ]\n"
                  + "}")
          }
      )
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "CREATED")})
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public ResponseEntity create(@RequestBody String content,
      UriComponentsBuilder uriComponentsBuilder) {
    Element element = Deserializer.getElement(content);
    try {
      ScopedIdentifier scopedIdentifier = elementService
          .create(MdrRestApplication.getCurrentUser().getId(), element);
      UriComponents uriComponents =
          uriComponentsBuilder.path("/element/{urn}")
              .buildAndExpand(IdentificationHandler.toUrn(scopedIdentifier));
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(uriComponents.toUri());
      return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    } catch (IllegalAccessException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Returns the element with the given URN. If the provided string is not of the used URN format it
   * will be treated as a Namespace name.
   */
  @GetMapping("/{urn}")
  @Operation(summary = "Get Element by URN.",
      description = "",
      tags = {"Element"},
      security = @SecurityRequirement(name = "basicAuth"))
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public ResponseEntity read(@PathVariable(value = "urn") String urn) {
    try {
      Element element = elementService.read(MdrRestApplication.getCurrentUser().getId(), urn);
      return new ResponseEntity<>(element, HttpStatus.OK);
    } catch (NoSuchElementException nse) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Updates an existing Element and return its new URN.
   */
  @PutMapping("/{urn}")
  @Operation(summary = "Update an Element with one of the models Dataelement, Dataelementgroup"
      + " or Record",
      description = "",
      tags = {"Element"},
      security = @SecurityRequirement(name = "basicAuth"))
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "",
      content =
      @Content(
          schema = @Schema(oneOf = {DataElement.class, DataElementGroup.class, Record.class}),
          examples = {
              @ExampleObject(name = "DATAELEMENT", value = "{\n"
                  + "  \"definitions\": [\n"
                  + "    {\n"
                  + "      \"designation\": \"Geschlecht\",\n"
                  + "      \"definition\": \"Geschlecht des Patienten\",\n"
                  + "      \"language\": \"de\"\n"
                  + "    },\n"
                  + "    {\n"
                  + "      \"designation\": \"gender\",\n"
                  + "      \"definition\": \"patient´s gender\",\n"
                  + "      \"language\": \"en\"\n"
                  + "    }\n"
                  + "  ],\n"
                  + "  \"validation\": {\n"
                  + "    \"type\": \"STRING\",\n"
                  + "        \"text\": \n"
                  + "            {\n"
                  + "            \"useRegEx\": false,\n"
                  + "            \"regEx\": null,\n"
                  + "            \"useMaximumLength\": false\n"
                  + "            }\n"
                  + "\n"
                  + "    },\n"
                  + "  \"slots\": [\n"
                  + "    {\n"
                  + "      \"name\": \"slot_name\",\n"
                  + "      \"value\": \"slot_value\"\n"
                  + "    }\n"
                  + "  ],\n"
                  + "  \"conceptAssociations\": [\n"
                  + "    {\n"
                  + "      \"conceptId\": 1,\n"
                  + "      \"system\": \"ca_system\",\n"
                  + "      \"sourceId\": 1,\n"
                  + "      \"version\": \"ca_version\",\n"
                  + "      \"term\": \"ca_term\",\n"
                  + "      \"text\": \"ca_text\",\n"
                  + "      \"linktype\": \"undefined\",\n"
                  + "      \"scopedIdentifierId\": 1\n"
                  + "    }\n"
                  + "  ]\n"
                  + "}\n"),
              @ExampleObject(name = "DATAELEMENTGROUP", value = "{\n"
                  + "  \"definitions\": [\n"
                  + "    {\n"
                  + "      \"designation\": \"people\",\n"
                  + "      \"definition\": \"smartphone\",\n"
                  + "      \"language\": \"de\"\n"
                  + "    }\n"
                  + "    ],\n"
                  + "  \"members\": \n"
                  + "  {\n"
                  + "    \"dataElementGroups\": [\n"
                  + "        \n"
                  + "      ],\n"
                  + "   \"dataElements\": [\n"
                  + "      \"urn:1:dataelement:1:1\"\n"
                  + "  ],\n"
                  + "  \"records\": [\n"
                  + "      \n"
                  + "    ]\n"
                  + "  }\n"
                  + "  ,\n"
                  + "  \"slots\": [\n"
                  + "    {\n"
                  + "      \"name\": \"slot_DEG\",\n"
                  + "      \"value\": \"slot_value\"\n"
                  + "    }\n"
                  + "  ]\n"
                  + "}"),
              @ExampleObject(name = "RECORD", value = "{\n"
                  + "  \"definitions\": [\n"
                  + "    {\n"
                  + "      \"designation\": \"people\",\n"
                  + "      \"definition\": \"smartphone\",\n"
                  + "      \"language\": \"de\"\n"
                  + "    }\n"
                  + "    ],\n"
                  + "  \"members\": \n"
                  + "  {\n"
                  + "    \"dataElementGroups\": [\n"
                  + "        \n"
                  + "      ],\n"
                  + "   \"dataElements\": [\n"
                  + "      \"urn:1:dataelement:1:1\"\n"
                  + "  ],\n"
                  + "  \"records\": [\n"
                  + "      \n"
                  + "    ]\n"
                  + "  }\n"
                  + "  ,\n"
                  + "  \"slots\": [\n"
                  + "    {\n"
                  + "      \"name\": \"slot_DEG\",\n"
                  + "      \"value\": \"slot_value\"\n"
                  + "    }\n"
                  + "  ]\n"
                  + "}")
          }
      )
  )
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public ResponseEntity update(@RequestBody String content, @PathVariable("urn") String oldUrn,
      UriComponentsBuilder uriComponentsBuilder) {
    Element element = Deserializer.getElement(content);
    Identification oldIdentification = IdentificationHandler.fromUrn(oldUrn);
    if (oldIdentification == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (oldIdentification.getStatus() == Status.RELEASED && (
        element.getIdentification().getStatus() == Status.STAGED
            || element.getIdentification().getStatus() == Status.DRAFT)) {
      return new ResponseEntity<>("Status change from released to draft or staged not allowed.",
          HttpStatus.BAD_REQUEST);
    }

    element.setIdentification(oldIdentification);
    try {
      Identification identification = elementService
          .update(MdrRestApplication.getCurrentUser().getId(), element);
      UriComponents uriComponents =
          uriComponentsBuilder.path("/element/{urn}").buildAndExpand(identification.getUrn());
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(uriComponents.toUri());
      return new ResponseEntity<>(httpHeaders, HttpStatus.NO_CONTENT);
    } catch (IllegalAccessException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (UnsupportedOperationException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
  }

  /**
   * Delete an element.
   * Only drafts are really deleted. Released elements are marked as outdated.
   * @return
   */
  @DeleteMapping("/{urn}")
  @Operation(summary = "Delete Element by URN.",
      description = "",
      tags = {"Element"},
      security = @SecurityRequirement(name = "basicAuth"))
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public ResponseEntity delete(@PathVariable(value = "urn") String urn) {
    try {
      elementService.delete(MdrRestApplication.getCurrentUser().getId(), urn);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (NoSuchElementException nse) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }


  /**
   * Releases the element with the given URN.
   */
  @PatchMapping("/{urn}/release")
  @Operation(summary = "Release Element by URN.",
      description = "",
      tags = {"Element"},
      security = @SecurityRequirement(name = "basicAuth"))
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public ResponseEntity release(@PathVariable(value = "urn") String urn) {
    try {
      elementService.release(MdrRestApplication.getCurrentUser().getId(), urn);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (IllegalArgumentException | IllegalStateException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (NoSuchElementException nse) {
      return new ResponseEntity<>(nse.getMessage(), HttpStatus.NOT_FOUND);
    }
  }
}
