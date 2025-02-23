package com.packt.modern.api.hateoas;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.packt.modern.api.controller.AddressController;
import com.packt.modern.api.entity.AddressEntity;
import com.packt.modern.api.model.Address;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

/**
 * @author : github.com/sharmasourabh
 * @project : Chapter04 - Modern API Development with Spring and Spring Boot Ed 2
 **/
@Component
public class AddressRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<AddressEntity, Address> {

  /**
   * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and
   * resource type.
   */
  public AddressRepresentationModelAssembler() {
    super(AddressController.class, Address.class);
  }

  /**
   * Coverts the Address entity to resource
   *
   * @param entity
   */
  @Override
  public Address toModel(AddressEntity entity) {
    Address resource = createModelWithId(entity.getId(), entity);
    BeanUtils.copyProperties(entity, resource);
    resource.setId(entity.getId().toString());
    // Self link generated by createModelWithId has missing api path. Therefore, generating additionally.
    // can be removed once fixed.
    resource.add(
        linkTo(methodOn(AddressController.class).getAddressesById(entity.getId().toString()))
            .withSelfRel());
    return resource;
  }

  /**
   * Coverts the collection of Product entities to list of resources.
   *
   * @param entities
   */
  public List<Address> toListModel(Iterable<AddressEntity> entities) {
    if (Objects.isNull(entities)) {
      return List.of();
    }
    return StreamSupport.stream(entities.spliterator(), false).map(this::toModel)
        .collect(toList());
  }

}
