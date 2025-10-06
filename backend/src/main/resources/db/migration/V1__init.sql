-- 建表
CREATE TABLE IF NOT EXISTS users (
  id        VARCHAR(20) PRIMARY KEY,
  username  VARCHAR(50) UNIQUE NOT NULL,
  password  VARCHAR(255)       NOT NULL,
  name      VARCHAR(100)       NOT NULL,
  role      VARCHAR(20)        NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
  class_id        VARCHAR(20) PRIMARY KEY,
  name      VARCHAR(200) NOT NULL,
  semester  VARCHAR(20)  NOT NULL,
  teacher   VARCHAR(100) NOT NULL
);

-- 索引（V1 只會跑一次，不需要 IF NOT EXISTS）
CREATE INDEX idx_courses_sem ON courses (semester);

-- 初始種子（idempotent）
INSERT INTO users(id, username, password, name, role) VALUES
('1','student','$2a$10$WalYTCVTP3Tbr2.N0P/8i.q5J2a2q.ZqGUc7tZFaYWejHMBJ0SsEu','學生小明','student'), -- student123
('2','ta','$2a$10$uy0BLmtawnvN78hpADURdO36fA5IuPeNGqu.Fh0olHM1rWxyBysGG','李助教','TA'), -- ta123
('3','teacher','$2a$10$wBT62IY7aGXqgcon4.eir.yoLWuiZoLImGuR/eF6Wbc6uCK8hU71C','王老師','teacher'), -- teacher123
('4','root','$2a$10$S8DrIFWV0WAf3TL5kxsWmedYbRqpcdukT.Zj2Ta2u71VoiOGS/Dx6','系統管理員','ROOT') -- root123
ON DUPLICATE KEY UPDATE name=VALUES(name), role=VALUES(role);

INSERT INTO courses(class_id, name, semester, teacher) VALUES
('CLS111','計算機概論','101-1','王老師'),
('CLS112','資料庫','101-2','王老師'),
('CLS113','計算機結構','102-1','李老師'),
('CLS114','嵌入式微處理機概論','102-2','陳老師')
ON DUPLICATE KEY UPDATE name=VALUES(name), semester=VALUES(semester), teacher=VALUES(teacher);
