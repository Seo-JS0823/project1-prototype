CREATE VIEW V_INDUSTRY_ID AS
SELECT
   M.Major_code || '-' || S.sub_code AS industry_id,
   M.Major_name,
   S.sub_name
FROM
   INDUSTRY_MAJOR M, INDUSTRY_SUB S
WHERE
   M.major_code = S.major_code
ORDER BY industry_id asc;