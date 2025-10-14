-- V2__seed_roles_users_and_members.sql
-- Seed roles, admin+user, and sample members
-- Replace password_hash values with your own BCrypt hashes if you prefer.

-- Insert roles
INSERT INTO surestdatabase.role (id, name)
VALUES
  (gen_random_uuid(), 'ROLE_ADMIN'),
  (gen_random_uuid(), 'ROLE_USER')
ON CONFLICT (name) DO NOTHING;

 Insert INTO users (admin + user)
 Example bcrypt hashes (generated earlier for demonstration):
 Admin password: Admin@123 -> $2b$12$A/v67jBCWcMaXYRAGlc.F.vcrVXRWHiFelfZKEOxUAtMNWf8YxB/u
 User  password: User@123  -> $2b$12$ZAufyDFKolDdk.V76iJ2I.KD/3PblKqLyMm4LetGPxVayfKfoWuDS

-- Admin
INSERT INTO surestdatabase."app_user" (id, username, password_hash, role_id)
SELECT gen_random_uuid(), 'admin', '$2b$12$A/v67jBCWcMaXYRAGlc.F.vcrVXRWHiFelfZKEOxUAtMNWf8YxB/u', r.id
FROM surestdatabase.role r
WHERE r.name = 'ROLE_ADMIN'
  AND NOT EXISTS (SELECT 1 FROM surestdatabase."app_user" u WHERE u.username = 'admin');

-- Regular user
INSERT INTO surestdatabase."user" (id, username, password_hash, role_id)
SELECT gen_random_uuid(), 'user', '$2b$12$ZAufyDFKolDdk.V76iJ2I.KD/3PblKqLyMm4LetGPxVayfKfoWuDS', r.id
FROM surestdatabase.role r
WHERE r.name = 'ROLE_USER'
  AND NOT EXISTS (SELECT 1 FROM surestdatabase."user" u WHERE u.username = 'user');

-- Sample members
INSERT INTO surestdatabase.member (id, first_name, last_name, date_of_birth, email)
VALUES
  (gen_random_uuid(), 'Alice', 'Singh', '1990-05-12', 'alice.singh@example.com'),
  (gen_random_uuid(), 'Rahul', 'Kumar', '1985-11-03', 'rahul.kumar@example.com'),
  (gen_random_uuid(), 'Priya', 'Patel', '1992-08-25', 'priya.patel@example.com')
ON CONFLICT (email) DO NOTHING;
