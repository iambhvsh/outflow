# Changelog

All notable changes to this project will be documented in this file.

## [1.1.0] - 2026-08-25

### Added

- Insights screen with today's totals, a weekly spending chart, this month's spending by category, and all-time totals.
- Tap a column in the weekly chart for that day's figures. Days without spending are left out.
- Settings screen.
- Floating navigation toolbar for the three screens, with the add button as its last item.
- Light, dark, and true black themes, plus an automatic mode that follows the system.
- Eight accent colours, and dynamic colour taken from the wallpaper on Android 12 and later.
- Choice of currency between the Indian rupee, US dollar, euro, and Japanese yen.
- Large figures on tiles and chart axes abbreviated, as ₹18.32K, ₹1.5L, or $1.5M.
- Tap a flow to edit it.
- Undo after deleting a flow.
- Confirmation prompt before a swipe deletes a flow.
- Sixteen preset categories, each with its own icon and colour.
- Flows grouped under the day they happened on, with that day's net beside the date.
- Delete everything in Settings, which reports how many flows will be removed.
- Empty states describing what will appear on Flows and Insights once flows are recorded.
- Loading indicator while flows are being read.

### Changed

- Rebuilt the interface with Material 3 Expressive.
- Redesigned the new flow sheet: direction is a dropdown beside the title.
- Categories are chosen from a list instead of typed. Categories typed in 1.0.0 that match no preset now read as Other.
- A title is optional. A flow saved without one is labelled with its category.
- Amounts are grouped by digit and no longer padded to two decimals: ₹1,23,456 for rupees, $123,456 for the rest.
- Swiping to delete now works right to left only.
- Totals, counts, and category breakdowns are calculated by the database, and the list loads 80 flows at a time as it is scrolled, so the app opens and scrolls at the same speed with any number of flows recorded.
- Existing flows are carried over untouched when upgrading from 1.0.0.

### Fixed

- Flows recorded in the same millisecond no longer swap places in the list between reads.

## [1.0.0] - 2026-08-23

### Added

- Record a flow with a title, an exact amount, and a category.
- Tag every flow as an inflow or an outflow.
- Summary card showing total in, total out, and net balance.
- Reverse-chronological list of every flow recorded.
- Swipe a flow horizontally to delete it.
- Pitch-black theme with an edge-to-edge layout, micro-animations, and segmented buttons.
- Google Sans Flex typography.
- Local storage in an embedded SQLite database, with no tracking and no cloud sync.
