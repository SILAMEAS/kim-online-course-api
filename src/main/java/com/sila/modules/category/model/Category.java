package com.sila.modules.category.model;

import com.sila.share.core.entity.AbstractAuditable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category extends AbstractAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long id;

  @Column(nullable = false, unique = true)
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;
}
