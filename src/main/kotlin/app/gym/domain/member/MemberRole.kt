package app.user.domain.member

enum class MemberRole(
    var value: String
) {
    Admin("ROLE_ADMIN"),
    Manager("ROLE_MANAGER"),
    Member("ROLE_MEMBER"),
    Guest("ROLE_GUEST");
}