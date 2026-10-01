# Monitoring refinement / raw measurement history (2026-10-01)

## Source audit (read-only)

Source: `../private-data/healing_spot_stress_emotional_summary.xlsx`.
SHA-256: `d0f81649162ee0177093365a27c5a53e51ec152135b2d70627c9f000185cb31d`.
The original was not edited, copied, moved, or recalculated. ZIP/XML inspection and the Apache POI parser only read it.

`01_원자료` has these 18 columns (the slash below represents the actual newline in a header):

1. 참여자
2. 날짜
3. 코스
4. 체험 공간
5. Baseline / 스트레스
6. Baseline / 정서적 안정성
7. HS1 / 스트레스
8. HS1 / 정서적 안정성
9. HS2 / 스트레스
10. HS2 / 정서적 안정성
11. HS3 / 스트레스
12. HS3 / 정서적 안정성
13. HS4 / 스트레스
14. HS4 / 정서적 안정성
15. HS5 / 스트레스
16. HS5 / 정서적 안정성
17. HS6 / 스트레스
18. HS6 / 정서적 안정성

There are 63 data rows (64 including the header), for 11 participants:

| Participant | Rows |
|---|---:|
| P001 | 2 |
| P002 | 3 |
| P003 | 6 |
| P004 | 7 |
| P005 | 6 |
| P006 | 8 |
| P007 | 8 |
| P008 | 4 |
| P009 | 8 |
| P010 | 8 |
| P011 | 3 |

Each row is one participant's dated Course measurement record, with shared Baseline values and one or two Spot measurements. It is **not proven to be a visit event**. `체험 공간` preserves the recorded route, including reversed Spot order. Course values are `HC-A (A 코스)` (18 rows), `HC-B (B 코스)` (24), and `HC-C (C 코스)` (21).

Dates are ISO calendar dates, from 2026-08-14 to 2026-09-04, without times. There are no duplicate participant+date or participant+date+Course keys. There is no explicit session/회차 column. Accordingly the UI uses **측정일 / 측정 기록**, never manufactured `1회차` or visit counts. Date ordering is appropriate for comparing these dated observations, but implies neither equal intervals nor daily observation. Graph x positions use actual elapsed days.

| Spot | Present stress/emotional pairs | Missing rows |
|---|---:|---:|
| HS1 | 18 | 45 |
| HS2 | 17 | 46 |
| HS3 | 22 | 41 |
| HS4 | 23 | 40 |
| HS5 | 20 | 43 |
| HS6 | 21 | 42 |

There are 121 Spot pairs. All present source Spot values are stress/emotional pairs. Missing non-Course Spots are not zeros. Both Baselines are missing for P005/2026-08-24, P006/2026-08-31, P008/2026-08-14, and P009/2026-08-19. Their real Post values are retained; change/rate is unavailable. No source Baseline equals zero. For future zero Baselines, rates are unavailable (not invented zero or infinity); Post and arithmetic change remain visible.

All four workbook sheets contain literal values, not formulas. Reconciliation of all `02` Member×Spot Summary rows and all six `03` overall rows matched within 1e-9:

- Stress reduction rate: `(Baseline - Post) / Baseline * 100`.
- Emotional increase rate: `(Post - Baseline) / Baseline * 100`.
- Per-member Summary: average of valid record rates, with matching valid counts.
- Overall Summary: unweighted mean of participant means, with matching participant/valid measurement counts. It is not a pooled raw-row mean.
- History change: `Post - Baseline` for both metrics, explicitly labeled in tables.

This reconciliation is audit evidence only. Neither Summary table is recalculated or overwritten.

## Domain and import safety

Raw data is separate from Summary. Three explicit SQL tables are defined in `healing-measurement-schema.sql`:

- `healing_measurement_import_batch`: source filename/SHA, target Site, timestamp and counts.
- `healing_measurement_session`: Member/Course FKs, source row, date, source route and two nullable Baselines. “Session” here means the validated source record, not a visit count.
- `healing_spot_measurement`: Session/Spot FKs and nullable exact Post values.

The application uses JDBC read records/repository rather than new JPA entities: existing production `ddl-auto=update` must not implicitly create raw tables. No startup migration/import or DataInitializer was added. If raw schema is absent, the query returns no history. No public endpoint or Landing DTO contains raw identity/history.

`RawMeasurementParser` validates exact headers, literal numeric values, ISO dates, Participant codes, Course/Spot/route consistency, and refuses duplicate participant+date records whose semantics would require review. DECIMAL(38,18) preserves source precision. There is no rate cap.

`./gradlew healingMeasurementImport --args='--file <original-path> --site-id <verified-id> --dry-run'`

The opt-in CLI uses `HEALING_IMPORT_JDBC_URL`, `HEALING_IMPORT_DB_USER`, and `HEALING_IMPORT_DB_PASSWORD`; only a localhost/127.0.0.1 MySQL URL is accepted. `--write` is a separate explicit mode, requiring already-reviewed schema. Credentials are not logged. Dry-run uses a read-only transaction and rollback. Write is atomic with rollback on failure. Mapping checks Member participantNo, existing Site Course codes and Spot code/name/Course. Same SHA/Site is a no-op; a different source cannot silently overwrite or append to existing raw data. Unique source-row and Member/date/Course constraints add protection. FK cascades preserve existing parent-delete behavior.

## Local execution

Read-only audit confirmed localhost:3306/manage, existing Summary import Site 1, 6 overall and 60 Member Summary rows, and no raw tables.
Dry-run succeeded: schemaReady=false, 63 records, 121 Spot measurements.
After reporting the schema and dry-run result and receiving local execution approval, the three raw tables were created and CLI write committed 63/121 rows. No Production/Railway connection, schema operation or import was performed. Original Excel SHA is unchanged.

## UI and reuse

- Phase 1: the 2.5px non-scaling Course SVG stroke was clipped by the SVG edge; a 2px rectangle-only inset keeps the stroke inside. Photo/data coordinates are unchanged.
- Header logo uses Landing's 220×46 desktop and 200×42 mobile dimensions, with aspect ratio/object-fit retained.
- Existing good `#347553`, bad `#b4534b`, neutral `#86743f` colors are used on result text and bars. Labels/counts remain neutral. Missing has a separate gray state.
- `MeasurementHistoryQueryService` / `MeasurementHistoryView` are reusable by Member ID and Site, not bound to Admin templates. Future participant access must derive the Member ID from authenticated identity, not accept arbitrary public IDs.
- Admin Member Data keeps Summary cards, followed by a six-Spot selector and two Baseline/Post date graphs with numeric tables.
- Monitoring keeps Summary comparisons and exposes the same history renderer only for a selected individual, in a collapsible section. Overall has no raw graph/aggregation.
- First Spot with actual records is selected deterministically; absent records show `측정 기록 없음`. Missing Baseline points are not drawn or connected across gaps.
- Spot detail modal retains Summary, images/gallery/map and valid-measurement terminology; raw graphs are not added there.

## Browser QA

Automated verification completed: `./gradlew test` — 314 tests, 0 failures/errors/skips; `node --test src/test/js/*.test.js` — 96 passed; `node --check` on all five changed/new application JS files passed; `git diff --check` passed. New tests cover synthetic raw headers, duplicate dates, invalid numeric data, nullable Baselines, mapping failures, dry-run, duplicate source, transactional rollback, exact chronological query results, Spot grouping, history selector/rendering and metric semantic colors. Existing Landing/Member/Monitoring tests remain passing.

Post-import read-only local verification: Summary rows 6/60 unchanged; raw rows 63/121; four missing-Baseline records. The original SHA was checked again and is unchanged. Visual browser QA for the newly added UI has not been performed by the agent; the following items remain for the user's browser. Restart the local application with the updated build if an older process is running.

1. At 100% zoom, confirm all four HC-B border edges; HS3/HS4 photos and HC-A/HC-C positions unchanged.
2. Compare Landing/Admin/Monitoring logo dimensions on desktop and mobile; no cropping.
3. In both metrics, verify positive-effect green, negative-effect red, zero neutral, missing gray across cards, spatial labels and Spot modal values. Labels/counts stay neutral.
4. Confirm overall actual Summary and Participant Summary selection still agree across UI components; no overall date/session graph.
5. Select different Participants, open 상세 분석 → 측정 기록, change HS1–HS6; verify dates, Baseline/Post graphs and numeric tables change together.
6. Open Admin Member Data: Summary cards remain; history matches the same Member/Spot in Monitoring. Test a Spot without records.
7. Check the four missing-Baseline records: real Post visible, no synthetic Baseline/rate. Check zero Summary remains distinct from missing.
8. Verify Site switching, Spot photos/gallery, map, Landing anonymous effects and Member detail/data navigation.

No commit, push or merge was performed.

## Changed files for these phases

- Presentation: `static/css/style.css`, `static/js/home-spatial.js`, `static/js/home-survey-analysis.js`, `static/js/home-map.js`, `templates/home.html`, `templates/admin/member-data.html`, new `static/css/measurement-history.css` and `static/js/measurement-history.js`.
- Read flow: `HomeController`, `AdminController`, new `HealingMeasurementRecord`, `HealingMeasurementRepository`, `MeasurementHistoryQueryService`, `MeasurementHistoryView`.
- Explicit import: `build.gradle`, new `RawMeasurementSource`, `RawMeasurementParser`, `RawMeasurementDatabaseImporter`, `RawMeasurementImportCli`, SQL schema and this audit report.
- Tests: `RawMeasurementTests`, `MeasurementHistoryQueryTests`, `HealingEffectUiTests`, `measurement-history.test.js`, `home-map.test.js`, `home-survey-analysis.test.js`.
- Prior uncommitted Summary wiring/DTO/tests were preserved; those existing changes also remain in the working tree.
