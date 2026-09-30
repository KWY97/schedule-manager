# Local import and UI validation — 2026-09-30

## Local data preparation

- Branch: `feature/healing-effect-data`.
- Connection: `localhost:3306/manage`, explicitly connected via loopback `127.0.0.1`.
- Site 1 was discovered by querying Site/Course/Spot relationships, not assumed.
- Three effect tables already existed at the start of this session, all empty. No CREATE/DROP/ALTER was executed.
- SHOW CREATE TABLE and Hibernate schema validation confirmed Entity-compatible columns, FK/indexes,
  `DECIMAL(38,18)`, and unique constraints. All relevant tables use InnoDB.
- A local pre-import backup was saved outside Git at `/tmp/healing-local-before.sql` with mode 0600.
- Pre-import counts: overall 0, participant 0, batch 0.
- Source stayed at `../private-data/healing_spot_stress_emotional_summary.xlsx`.
- SHA-256: `d0f81649162ee0177093365a27c5a53e51ec152135b2d70627c9f000185cb31d`.
- Dry-run: `DRY_RUN`, `schemaReady=true`, 6/60 planned rows, zero validation errors.
- First explicit local write: `IMPORTED`, 6/60 rows. Batch count 1.
- Second identical write: `ALREADY_IMPORTED`, zero new rows. Final counts remain 6/60/1.
- Checksums of every pre-existing non-effect table remained unchanged after import and verification.

| Spot | Stored stress reduction | Stored emotional increase | Display |
|---|---:|---:|---|
| HS1 | 19.500108327673271000 | 54.267121115714403000 | 19.5% / 54.3% |
| HS6 | 21.213120597090558000 | 200.422124968168790000 | 21.2% / 200.4% |

JDBC audit verified participantNo-to-Member FK mapping for participant numbers 1, 4, 11.
The six missing combinations (1: HS5/HS6, 5: HS1/HS2, 11: HS1/HS2) have no rows.
A standalone read-only Hibernate/JPA verification used the actual Query Service, compared all
6 overall and 60 participant records to parsed source BigDecimals, verified all 6 missing pairs,
ordering and one-decimal displays, then rolled back. It did not start Spring Boot or a web server.
The new anonymous selection was also checked against the source and repeated deterministically.

## UI behavior

- ImportBatch selects the dataset Site. No Site ID or example Member ID is hardcoded in the UI.
- Public example: first Member in participantNo order with all six distinct Spot summaries.
  If none exists, show `데이터 준비 중` rather than a partial/random/example-number fallback.
- Public model carries only `HealingEffectView` lists/maps: Spot code/name, two raw rates and display
  strings, two valid-session counts, and hasMeasurement. No participant identity field exists.
- Overall values are supplied through HomeController → Query Service → Model → Thymeleaf attributes
  → existing carousel JS. Photos, cross-fade, course layout and original HS3 CTA image are retained.
- Personal card: existing photo, `참가자 예시`, two metric buttons, six independent signed bars/Spot
  buttons and an active Spot summary. No Baseline, invented Before/After or connected trend line.
- Personal browsing advances every 6.5 seconds; clicks take effect immediately and hold 8.5 seconds.
  Reduced motion stops automatic browsing; keyboard buttons and ARIA selection states remain usable.
- Negative values and values above 100% retain their source values. Bars scale for comparison only;
  scaling does not alter stored/displayed percentages. A zero rate remains distinct from missing data.
- Protected Admin detail shows six ordered Spot cards and `측정 없음` for missing rows.
- All three requested Hero descriptions are removed; empty paragraphs and old graph CSS are removed.
  Existing background images/band heights remain, with zero bottom margin on the final title.
- Monitoring EEG/Pulse/biomarker and recommended-course samples remain unchanged.

## Verification and remaining checks

- Gradle: 305 tests passed, 0 failures/errors/skips (all 298 existing tests plus 7 UI tests).
  Existing Landing design tests were updated for intentional removal of sample content.
- Added Node interaction tests: 5 passed (metric/Spot selection, signed/zero/uncapped values,
  carousel synchronization, empty data, reduced motion and browsing).
- `node --check src/main/resources/static/js/landing.js`: passed.
- Rendered HTML privacy test: participant codes, memberId, participantNo, login IDs, names and phones absent.
- `git diff --check`: passed.
- No localhost:8080 server start/stop, Railway access, production SQL/import/deploy, or commit/push/merge.
- No QA repository writes or private Excel edits/copies. Source hash remained unchanged.

User browser checklist (use the user's IntelliJ-managed server):
1. Confirm the running application has loaded the changed code, then open `/` while logged out.
2. Check HS1 through HS6 image/selector/rate synchronization, including HS6 200.4%.
3. Try both personal metrics and all six Spot buttons; inspect negative rates and the click hold.
4. Enable reduced motion and confirm automatic browsing stops.
5. Check desktop/mobile card layout, three Hero backgrounds and bottom CTA photo.
6. As Admin, inspect participant details including the six known missing combinations.
7. Inspect public page source/DOM and confirm no participant identity is present.

Before production: review the diff; verify production-specific Site/Member mappings and schema;
back up that database; authorize a separate production schema/import plan and safe deployment order;
run target-specific dry-run, idempotency and smoke/privacy checks. The current CLI deliberately
rejects remote JDBC URLs, so production importing requires a separately reviewed execution method.
