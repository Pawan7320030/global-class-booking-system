INSERT INTO users (id, name, email, role, timezone, created_at)
VALUES
(1, 'Rahul Teacher', 'teacher@test.com', 'TEACHER', 'Asia/Kolkata', NOW()),
(2, 'John Parent', 'parent@test.com', 'PARENT', 'America/New_York', NOW())
ON DUPLICATE KEY UPDATE email = email;

INSERT INTO courses (id, title, description, created_at)
VALUES
(1, 'Minecraft Coding', 'Live coding class for kids', NOW()),
(2, 'Roblox Game Design', 'Game design class for kids', NOW())
ON DUPLICATE KEY UPDATE title = title;
