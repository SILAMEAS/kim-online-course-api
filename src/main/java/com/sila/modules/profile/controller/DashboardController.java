package com.sila.modules.profile.controller;

import com.sila.modules.profile.dto.res.DashboardResponse;
import com.sila.modules.profile.dto.res.DashboardUserResponse;
import com.sila.modules.profile.service.DashboardService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping
  @PreAuthorization({ROLE.ADMIN})
  @Operation(
      summary = "DashboardResponse",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = DashboardResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<DashboardResponse> dashboard() {
    return ResponseEntity.ok(dashboardService.getDashboard());
  }

  @GetMapping("/students")
  @PreAuthorization({ROLE.STUDENT,ROLE.ADMIN,ROLE.INSTRUCTOR})
  @Operation(
      summary = "DashboardResponse",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Users retrieved successfully",
              content = @Content(schema = @Schema(implementation = DashboardUserResponse.class))),
          @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<DashboardUserResponse> dashboardStudent() {
    return ResponseEntity.ok(dashboardService.getDashboardUser());
  }
}
