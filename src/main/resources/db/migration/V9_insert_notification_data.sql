INSERT INTO jjanpot.notification_template (`type`, title, body, created_at, updated_at)
SELECT *
FROM (
    SELECT 'ENCOURAGE', '앗, 혹시…⚠️', '오늘 인증, 깜빡하신 거 아니죠? 지금 잠깐이면 충분해요.', NOW(), NOW()
        UNION ALL
    SELECT 'ENCOURAGE', '✨ 꾸준함을 유지할 시간이에요', '작은 기록이 모여 큰 변화를 만들어요. 오늘 인증도 놓치지 마세요!', NOW(), NOW()
        UNION ALL
    SELECT 'ENCOURAGE', '오늘 아직 인증 안 했어요 👀', '가볍게 인증하고 오늘 마무리해요.', NOW(), NOW()
) AS tmp (`type`, title, body, created_at, updated_at)
WHERE NOT EXISTS (
    SELECT 1 FROM jjanpot.notification_template WHERE `type` = 'ENCOURAGE'
);
