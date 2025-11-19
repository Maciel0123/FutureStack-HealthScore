ALTER TABLE check_ins
DROP CONSTRAINT IF EXISTS FK_CHECK_INS_ON_USER;

ALTER TABLE check_ins
DROP CONSTRAINT IF EXISTS fk_check_ins_on_usr;

ALTER TABLE check_ins
    ADD CONSTRAINT fk_check_ins_on_user
        FOREIGN KEY (user_id)
            REFERENCES usuario (id)
            ON DELETE CASCADE;

ALTER TABLE recommendation_model
DROP CONSTRAINT IF EXISTS fk_recommendation_user;

ALTER TABLE recommendation_model
    ADD CONSTRAINT fk_recommendation_user
        FOREIGN KEY (user_id)
            REFERENCES usuario (id)
            ON DELETE CASCADE;

ALTER TABLE recommendation_model
DROP CONSTRAINT IF EXISTS fk_recommendation_checkin;

ALTER TABLE recommendation_model
    ADD CONSTRAINT fk_recommendation_checkin
        FOREIGN KEY (check_in_id)
            REFERENCES check_ins (id)
            ON DELETE CASCADE;
