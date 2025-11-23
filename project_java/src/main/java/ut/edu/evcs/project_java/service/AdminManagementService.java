package ut.edu.evcs.project_java.service;

import java.util.List;

import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.web.dto.admin.CreateUserRequest;
import ut.edu.evcs.project_java.web.dto.admin.UpdateUserRequest;
import ut.edu.evcs.project_java.web.dto.admin.UserDetailDTO;
import ut.edu.evcs.project_java.web.dto.admin.StationDetailDTO;
import ut.edu.evcs.project_java.web.dto.admin.ChargingPointDetailDTO;
import ut.edu.evcs.project_java.web.dto.admin.SubscriptionPlanDTO;
import ut.edu.evcs.project_java.web.dto.admin.CreateSubscriptionRequest;
import ut.edu.evcs.project_java.web.dto.admin.UserSubscriptionDTO;

public interface AdminManagementService {

    // ============ User Management ============
    List<UserDetailDTO> getAllUsers();

    UserDetailDTO getUserById(String userId);

    User createUser(CreateUserRequest request);

    User updateUser(String userId, UpdateUserRequest request);

    void deleteUser(String userId);

    List<UserDetailDTO> getUsersByType(String userType);

    // ============ Station & Charging Point Management ============
    List<StationDetailDTO> getAllStationsWithDetails();

    StationDetailDTO getStationDetail(String stationId);

    List<ChargingPointDetailDTO> getChargingPointsByStation(String stationId);

    void toggleChargingPoint(String pointId, boolean online);

    // ============ Subscription Plan Management ============
    List<SubscriptionPlanDTO> getAllSubscriptionPlans();

    SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO);

    SubscriptionPlanDTO updateSubscriptionPlan(String planId, SubscriptionPlanDTO planDTO);

    void deleteSubscriptionPlan(String planId);

    // ============ User Subscription Management ============
    UserSubscriptionDTO assignSubscriptionToUser(String userId, CreateSubscriptionRequest request);

    List<UserSubscriptionDTO> getUserSubscriptions(String userId);

    void cancelUserSubscription(String subscriptionId);
}
