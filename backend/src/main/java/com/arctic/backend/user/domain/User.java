package com.arctic.backend.user.domain;

import com.arctic.backend.common.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "v1_users", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(
                name = "uk_user_email",
                columnNames = {"email"}
        )
})
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", length = 512)
    private String password;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;

    @Column(name = "phone", length = 11)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRole role;

    @Column(name = "profile_image", length = 300)
    private String profileImage;

    @Column(name = "primary_tenant_id")
    private Long primaryTenantId;

    private User(String email, String password, String nickname, String phone) {
        this.email = normalizeEmail(email);
        this.password = password;
        this.nickname = normalizeNickname(nickname);
        this.phone = normalizePhone(phone);
        this.role = UserRole.USER;
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static String normalizeNickname(String nickname) {
        return nickname == null ? null : nickname.trim();
    }

    private static String normalizePhone(String phone) {
        return phone == null ? null : phone.replaceAll("\\D", "");
    }

    public static User create(String email, String password, String nickname, String phone) {
        return new User(email, password, nickname, phone);
    }

    public void updateEmail(String email) {
        this.email = email.trim().toLowerCase();
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname.trim();
    }

    public void changePrimaryTenant(Long tenantId) {
        this.primaryTenantId = tenantId;
    }

    public boolean hasPrimaryTenant() {
        return this.primaryTenantId != null;
    }
}