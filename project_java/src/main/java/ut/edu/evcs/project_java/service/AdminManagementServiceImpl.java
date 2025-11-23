package ut.edu.evcs.project_java.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ut.edu.evcs.project_java.domain.billing.Wallet;
import ut.edu.evcs.project_java.domain.station.ChargingPoint;
import ut.edu.evcs.project_java.domain.station.Station;
import ut.edu.evcs.project_java.domain.subscription.PlanType;
import ut.edu.evcs.project_java.domain.subscription.SubscriptionPlan;
import ut.edu.evcs.project_java.domain.subscription.SubscriptionStatus;
import ut.edu.evcs.project_java.domain.subscription.UserSubscription;
import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.domain.user.enums.UserType;
import ut.edu.evcs.project_java.repo.ChargingPointRepository;
import ut.edu.evcs.project_java.repo.ConnectorRepository;
import ut.edu.evcs.project_java.repo.StationRepository;
import ut.edu.evcs.project_java.repo.SubscriptionPlanRepository;
import ut.edu.evcs.project_java.repo.UserRepository;
import ut.edu.evcs.project_java.repo.UserSubscriptionRepository;
import ut.edu.evcs.project_java.repo.WalletRepository;
import ut.edu.evcs.project_java.web.dto.admin.ChargingPointDetailDTO;
import ut.edu.evcs.project_java.web.dto.admin.CreateSubscriptionRequest;
import ut.edu.evcs.project_java.web.dto.admin.CreateUserRequest;
import ut.edu.evcs.project_java.web.dto.admin.StationDetailDTO;
import ut.edu.evcs.project_java.web.dto.admin.SubscriptionPlanDTO;
import ut.edu.evcs.project_java.web.dto.admin.UpdateUserRequest;
import ut.edu.evcs.project_java.web.dto.admin.UserDetailDTO;
import ut.edu.evcs.project_java.web.dto.admin.UserSubscriptionDTO;

@Service
@Transactional
public class AdminManagementServiceImpl implements AdminManagementService {

    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final ChargingPointRepository chargingPointRepository;
    private final ConnectorRepository connectorRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminManagementServiceImpl(
            UserRepository userRepository,
            StationRepository stationRepository,
            ChargingPointRepository chargingPointRepository,
            ConnectorRepository connectorRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            UserSubscriptionRepository userSubscriptionRepository,
            WalletRepository walletRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.chargingPointRepository = chargingPointRepository;
        this.connectorRepository = connectorRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============ User Management ============

    @Override
    public List<UserDetailDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToUserDetailDTO).collect(Collectors.toList());
    }

    @Override
    public UserDetailDTO getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        return convertToUserDetailDTO(user);
    }

    @Override
    public User createUser(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại: " + request.getEmail());
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại: " + request.getUsername());
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setFullName(request.getFullName());
        newUser.setPhone(request.getPhone());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setType(UserType.valueOf(request.getUserType()));

        User savedUser = userRepository.save(newUser);

        // Create wallet for new user
        Wallet wallet = new Wallet();
        wallet.setOwnerUserId(savedUser.getId());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("VND");
        walletRepository.save(wallet);

        return savedUser;
    }

    @Override
    public User updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getUserType() != null) {
            user.setType(UserType.valueOf(request.getUserType()));
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDetailDTO> getUsersByType(String userType) {
        UserType type = UserType.valueOf(userType);
        List<User> users = userRepository.findByType(type);
        return users.stream().map(this::convertToUserDetailDTO).collect(Collectors.toList());
    }

    // ============ Station & Charging Point Management ============

    @Override
    public List<StationDetailDTO> getAllStationsWithDetails() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream().map(this::convertToStationDetailDTO).collect(Collectors.toList());
    }

    @Override
    public StationDetailDTO getStationDetail(String stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạm sạc với ID: " + stationId));

        StationDetailDTO dto = convertToStationDetailDTO(station);

        List<ChargingPoint> points = chargingPointRepository.findByStation(station);
        dto.setChargingPoints(points.stream()
                .map(this::convertToChargingPointDetailDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    public List<ChargingPointDetailDTO> getChargingPointsByStation(String stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạm sạc với ID: " + stationId));

        List<ChargingPoint> points = chargingPointRepository.findByStation(station);
        return points.stream()
                .map(this::convertToChargingPointDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleChargingPoint(String pointId, boolean online) {
        ChargingPoint point = chargingPointRepository.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm sạc với ID: " + pointId));

        point.setOnline(online);
        chargingPointRepository.save(point);
    }

    // ============ Subscription Plan Management ============

    @Override
    public List<SubscriptionPlanDTO> getAllSubscriptionPlans() {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll();
        return plans.stream().map(this::convertToSubscriptionPlanDTO).collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(planDTO.getName());
        plan.setPlanType(PlanType.valueOf(planDTO.getPlanType()));
        plan.setPrice(planDTO.getPrice());
        plan.setDurationDays(planDTO.getDurationDays());
        plan.setDiscountPercent(planDTO.getDiscountPercent() != null ? planDTO.getDiscountPercent() : BigDecimal.ZERO);
        plan.setFreeKwh(planDTO.getFreeKwh() != null ? planDTO.getFreeKwh() : BigDecimal.ZERO);
        plan.setPriorityAccess(planDTO.getPriorityAccess() != null ? planDTO.getPriorityAccess() : false);
        plan.setDescription(planDTO.getDescription());
        plan.setIsActive(planDTO.getIsActive() != null ? planDTO.getIsActive() : true);

        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return convertToSubscriptionPlanDTO(savedPlan);
    }

    @Override
    public SubscriptionPlanDTO updateSubscriptionPlan(String planId, SubscriptionPlanDTO planDTO) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói với ID: " + planId));

        if (planDTO.getName() != null)
            plan.setName(planDTO.getName());
        if (planDTO.getPlanType() != null)
            plan.setPlanType(PlanType.valueOf(planDTO.getPlanType()));
        if (planDTO.getPrice() != null)
            plan.setPrice(planDTO.getPrice());
        if (planDTO.getDurationDays() != null)
            plan.setDurationDays(planDTO.getDurationDays());
        if (planDTO.getDiscountPercent() != null)
            plan.setDiscountPercent(planDTO.getDiscountPercent());
        if (planDTO.getFreeKwh() != null)
            plan.setFreeKwh(planDTO.getFreeKwh());
        if (planDTO.getPriorityAccess() != null)
            plan.setPriorityAccess(planDTO.getPriorityAccess());
        if (planDTO.getDescription() != null)
            plan.setDescription(planDTO.getDescription());
        if (planDTO.getIsActive() != null)
            plan.setIsActive(planDTO.getIsActive());

        plan.setUpdatedAt(LocalDateTime.now());

        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return convertToSubscriptionPlanDTO(updatedPlan);
    }

    @Override
    public void deleteSubscriptionPlan(String planId) {
        if (!subscriptionPlanRepository.existsById(planId)) {
            throw new RuntimeException("Không tìm thấy gói với ID: " + planId);
        }
        subscriptionPlanRepository.deleteById(planId);
    }

    // ============ User Subscription Management ============

    @Override
    public UserSubscriptionDTO assignSubscriptionToUser(String userId, CreateSubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói với ID: " + request.getPlanId()));

        UserSubscription subscription = new UserSubscription();
        subscription.setUserId(userId);
        subscription.setPlan(plan);
        subscription.setStartDate(LocalDateTime.now());

        int duration = request.getDurationDays() != null ? request.getDurationDays() : plan.getDurationDays();
        subscription.setEndDate(LocalDateTime.now().plusDays(duration));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setKwhUsed(BigDecimal.ZERO);

        UserSubscription savedSubscription = userSubscriptionRepository.save(subscription);
        return convertToUserSubscriptionDTO(savedSubscription, user);
    }

    @Override
    public List<UserSubscriptionDTO> getUserSubscriptions(String userId) {
        List<UserSubscription> subscriptions = userSubscriptionRepository.findByUserId(userId);
        User user = userRepository.findById(userId).orElse(null);

        return subscriptions.stream()
                .map(sub -> convertToUserSubscriptionDTO(sub, user))
                .collect(Collectors.toList());
    }

    @Override
    public void cancelUserSubscription(String subscriptionId) {
        UserSubscription subscription = userSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy subscription với ID: " + subscriptionId));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setUpdatedAt(LocalDateTime.now());
        userSubscriptionRepository.save(subscription);
    }

    // ============ Helper Methods - DTO Conversions ============

    private UserDetailDTO convertToUserDetailDTO(User user) {
        UserDetailDTO dto = new UserDetailDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setUserType(user.getType().name());

        userSubscriptionRepository.findActiveSubscriptionByUserId(user.getId())
                .ifPresent(sub -> dto.setCurrentSubscriptionPlan(sub.getPlan().getName()));

        walletRepository.findByOwnerUserId(user.getId())
                .ifPresent(wallet -> dto.setWalletBalance(wallet.getBalance().doubleValue()));

        return dto;
    }

    private StationDetailDTO convertToStationDetailDTO(Station station) {
        List<ChargingPoint> points = chargingPointRepository.findByStation(station);
        long onlineCount = points.stream().filter(ChargingPoint::isOnline).count();

        StationDetailDTO dto = new StationDetailDTO();
        dto.setId(station.getId());
        dto.setName(station.getName());
        dto.setAddress(station.getAddress());
        dto.setLat(station.getLat());
        dto.setLng(station.getLng());
        dto.setStatus(station.getStatus() != null ? station.getStatus().name() : "UNKNOWN");
        dto.setAvailablePorts(station.getAvailablePorts());
        dto.setTotalChargingPoints(points.size());
        dto.setOnlineChargingPoints((int) onlineCount);

        return dto;
    }

    private ChargingPointDetailDTO convertToChargingPointDetailDTO(ChargingPoint point) {
        int connectorCount = connectorRepository.findByChargingPoint(point).size();
        int occupiedCount = (int) connectorRepository.findByChargingPoint(point)
                .stream().filter(c -> c.isOccupied()).count();

        ChargingPointDetailDTO dto = new ChargingPointDetailDTO();
        dto.setId(point.getId());
        dto.setCode(point.getCode());
        dto.setMaxPowerKW(point.getMaxPowerKW());
        dto.setOnline(point.isOnline());
        dto.setStationName(point.getStation() != null ? point.getStation().getName() : "N/A");
        dto.setConnectorCount(connectorCount);
        dto.setOccupiedConnectors(occupiedCount);

        return dto;
    }

    private SubscriptionPlanDTO convertToSubscriptionPlanDTO(SubscriptionPlan plan) {
        SubscriptionPlanDTO dto = new SubscriptionPlanDTO();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setPlanType(plan.getPlanType().name());
        dto.setPrice(plan.getPrice());
        dto.setDurationDays(plan.getDurationDays());
        dto.setDiscountPercent(plan.getDiscountPercent());
        dto.setFreeKwh(plan.getFreeKwh());
        dto.setPriorityAccess(plan.getPriorityAccess());
        dto.setDescription(plan.getDescription());
        dto.setIsActive(plan.getIsActive());

        return dto;
    }

    private UserSubscriptionDTO convertToUserSubscriptionDTO(UserSubscription subscription, User user) {
        UserSubscriptionDTO dto = new UserSubscriptionDTO();
        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUserId());
        dto.setUserName(user != null ? user.getFullName() : "Unknown");
        dto.setPlanId(subscription.getPlan().getId());
        dto.setPlanName(subscription.getPlan().getName());
        dto.setPlanType(subscription.getPlan().getPlanType().name());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setStatus(subscription.getStatus().name());
        dto.setKwhUsed(subscription.getKwhUsed());

        return dto;
    }
}
