-- src/main/resources/db/migration/V2__rename_courses_id_and_add_enrollments.sql

-- 3) 建立 enrollments 連接表（user 多對多 course）
CREATE TABLE IF NOT EXISTS enrollments (
  user_id  VARCHAR(20) NOT NULL,
  class_id VARCHAR(20) NOT NULL,
  role_in_course VARCHAR(20) NULL,
  joined_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, class_id),
  CONSTRAINT fk_enr_user  FOREIGN KEY (user_id)  REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_enr_class FOREIGN KEY (class_id) REFERENCES courses(class_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_enr_user (user_id),
  INDEX idx_enr_class (class_id)
) ENGINE=InnoDB;

-- 4) 依你 mock 的 enrollments 塞資料（請確認課程代碼一致）
-- '1': ['CLS101','CLS103'], '2':['CLS101'], '3':['CLS101','CLS102'], '4':['CLS101','CLS102','CLS103']
INSERT INTO enrollments (user_id, class_id, role_in_course) VALUES
('1','CLS111','student'), ('1','CLS113','student'),
('2','CLS111','TA'),
('3','CLS111','teacher'), ('3','CLS112','teacher'),
('4','CLS111','ROOT'), ('4','CLS112','ROOT'), ('4','CLS113','ROOT')
ON DUPLICATE KEY UPDATE role_in_course=VALUES(role_in_course);
