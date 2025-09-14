ackage com.lender.mortgage.repository;

import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndActiveTrue(String email);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByRoleAndActiveTrue(UserRole role);
    
    Page<User> findByActiveTrue(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.active = true AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findActiveUsersBySearch(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role IN :roles AND u.active = true")
    List<User> findByRolesAndActiveTrue(@Param("roles") List<UserRole> roles);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.active = true")
    long countByRoleAndActiveTrue(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date AND u.active = true")
    List<User> findUsersNotLoggedInSince(@Param("date") LocalDateTime date);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
}