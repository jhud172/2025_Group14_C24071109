package uk.ac.cf._5.group14.One_To_One.Users;

public enum Role {
    CLIENT,
    TRAINER,
    GYM_ADMIN,

    // Kept for future/compatibility if already present in the DB.
    PLATFORM_ADMIN,
    SUPER_ADMIN
}
