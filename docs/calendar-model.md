# Calendar Model Refactoring Documentation

## Overview

This document explains the calendar refactoring that eliminated map key mismatches by introducing a unified `CalendarDayModel` DTO and `CalendarDayModelBuilder` service. The refactoring ensures type-safe access to calendar data in Thymeleaf templates and prevents silent null returns that caused tasks and schedule occurrences to not display.

---

## Problem Statement

### Original Issue
The calendar views used map-based data structures with mismatched key types:
- **Controller**: Created maps with `LocalDate` objects as keys
  ```java
  Map<LocalDate, List<CalendarTask>> tasksByDate = ...;
  ```
- **Template**: Attempted to lookup using ISO date strings
  ```html
  <div th:each="task : ${tasksByDateIso[dayModel.isoDate]}">
  ```

### Symptoms
- **Silent failures**: Tasks and schedule occurrences didn't display on the calendar
- **No error messages**: Thymeleaf treated missing map entries as empty lists
- **Debugging difficulty**: Map key type mismatches were invisible at runtime

### Root Cause
The Thymeleaf template used `tasksByDateIso` map with string keys (`"2026-03-15"`), but the map was keyed by `LocalDate` objects. The expression `tasksByDateIso[dayModel.isoDate]` returned `null` instead of throwing an error, resulting in empty calendar cells even when data existed in the database.

---

## Solution Architecture

### Core Components

#### 1. CalendarDayModel DTO
**Location**: `src/main/java/.../CalendarData/CalendarDayModel.java`

A unified data transfer object representing a single day cell in the calendar:

```java
public class CalendarDayModel {
    private final LocalDate localDate;      // Java LocalDate object for date operations
    private final String isoDate;           // ISO-8601 formatted string (yyyy-MM-dd)
    private final int dayOfMonth;           // Day number (1-31)
    private final List<CalendarTask> tasks; // All tasks for this day
    private final List<ScheduleOccurrence> occurrences; // All schedule occurrences
    private final DayStatus status;         // Computed status (today/past/future)
```

**Key Benefits**:
- **Type safety**: All data pre-computed and accessible through simple getters
- **No map lookups**: Direct property access in templates (`dayModel.tasks`)
- **Defensive copying**: Lists are copied in constructor to prevent external modification
- **Automatic status computation**: Determines if day is today, past, future, etc.

#### 2. CalendarDayModelBuilder Service
**Location**: `src/main/java/.../CalendarData/CalendarDayModelBuilder.java`

A service responsible for building lists of `CalendarDayModel` objects:

```java
@Service
public class CalendarDayModelBuilder {
    
    public List<CalendarDayModel> buildMonthDays(
        int year, int month, LocalDate today,
        Map<LocalDate, List<CalendarTask>> tasksByDate,
        Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate
    ) {
        // Builds 28-31 CalendarDayModel objects for the month
    }
    
    public List<CalendarDayModel> buildWeekDays(
        LocalDate weekStart, LocalDate today,
        Map<LocalDate, List<CalendarTask>> tasksByDate,
        Map<LocalDate, List<ScheduleOccurrence>> occurrencesByDate
    ) {
        // Builds 7 CalendarDayModel objects for the week
    }
}
```

**Responsibilities**:
- Pre-compute all day data for a month or week
- Lookup tasks and occurrences from input maps (with type-safe `LocalDate` keys)
- Create defensive copies of task/occurrence lists
- Package data into `CalendarDayModel` objects

#### 3. Controller Integration
**Location**: `src/main/java/.../ScheduleData/CalendarController.java`

The controller now uses `CalendarDayModelBuilder` instead of passing raw maps:

**Before**:
```java
Map<LocalDate, List<CalendarTask>> tasksByDate = ...;
Map<LocalDate, List<ScheduleOccurrence>> occurrences = ...;
model.addAttribute("tasksByDateIso", toIsoDateKeyedMap(tasksByDate));
model.addAttribute("occurrencesByDateIso", toIsoDateKeyedMap(occurrences));
```

**After**:
```java
Map<LocalDate, List<CalendarTask>> tasksByDate = ...;
Map<LocalDate, List<ScheduleOccurrence>> occurrences = ...;
List<CalendarDayModel> calendarDays = calendarDayModelBuilder.buildMonthDays(
    year, month, today, tasksByDate, occurrences
);
model.addAttribute("calendarDays", calendarDays);
```

#### 4. Template Refactoring
**Location**: `src/main/resources/templates/calendar/month.html` and `week.html`

Templates now iterate over `calendarDays` list instead of performing map lookups:

**Before** (map-based approach):
```html
<div th:each="i : ${#numbers.sequence(1, daysInMonth)}"
     th:with="dateKey=${year + '-' + month + '-' + i},
              tasks=${tasksByDateIso[dateKey]}">
    <div th:each="task : ${tasks}">
        <!-- Silent null if dateKey doesn't match map key type -->
    </div>
</div>
```

**After** (list-based approach):
```html
<div th:each="dayModel : ${calendarDays}"
     th:with="tasks=${dayModel.tasks}">
    <div th:each="task : ${tasks}">
        <!-- Direct access, no key matching issues -->
    </div>
</div>
```

---

## Data Attribute Standardization

### Issue
Schedule occurrences had inconsistent `data-type` attributes:
- Some used `data-type="workout"`
- Some used `data-type="occurrence"`

This caused JavaScript selectors like `querySelector('[data-type="occurrence"]')` to fail intermittently.

### Solution
All schedule occurrences now use **`data-type="occurrence"`** consistently:

```html
<!-- Tasks -->
<div th:attr="data-type='task',
              data-id=${task.id},
              data-date=${dayModel.isoDate}">
    
<!-- Schedule Occurrences -->
<div th:attr="data-type='occurrence',
              data-id=${occ.id},
              data-date=${dayModel.isoDate}">
```

**Changes Made**:
- Removed all `data-type="workout"` attributes
- Standardized to `data-type="occurrence"` for ALL schedule items
- Templates affected: `month.html` and `week.html`

### JavaScript Impact
With standardized attributes, JavaScript code now works reliably:
```javascript
// Find all occurrences (previously failed when data-type='workout' was present)
document.querySelectorAll('[data-type="occurrence"]');

// Toggle completion status
function toggleTaskCompletion(taskElement) {
    const dataType = taskElement.dataset.type;
    if (dataType === 'task') {
        // Handle task completion
    } else if (dataType === 'occurrence') {
        // Handle occurrence completion (works for ALL occurrences now)
    }
}
```

---

## Testing Strategy

### Unit Tests
**Location**: `src/test/java/.../CalendarTests/CalendarDayModelBuilderTest.java`

**Coverage**: 22 comprehensive tests verifying:
- Building month days (28-31 days depending on month)
- Building week days (always 7 days)
- Handling null/empty task and occurrence maps
- Defensive copying (modifications to input lists don't affect output)
- Day status computation (past-zero, past-incomplete, today, tomorrow, future)
- ISO date formatting (yyyy-MM-dd)
- Preserving task and occurrence order

**Example**:
```java
@Test
void buildMonthDaysReturnsCorrectNumberOfDays() {
    int year = 2026;
    int month = 3; // March has 31 days
    LocalDate today = LocalDate.of(2026, 3, 15);
    
    List<CalendarDayModel> days = builder.buildMonthDays(
        year, month, today, Map.of(), Map.of()
    );
    
    assertEquals(31, days.size());
    assertEquals("2026-03-01", days.get(0).getIsoDate());
    assertEquals("2026-03-31", days.get(30).getIsoDate());
}
```

### Integration Tests
**Location**: `src/test/java/.../CalendarTests/CalendarMonthWeekViewDataDisplayTest.java`

**Coverage**: 7 MVC tests verifying:
1. **Month view displays tasks and schedules correctly**
   - Verifies controller adds `calendarDays` to model
   - Checks model contains expected attributes
2. **Week view displays tasks and schedules correctly**
   - Verifies week view uses CalendarDayModelBuilder
   - Confirms 7 days of data are present
3. **Month view handles empty data correctly**
   - Tests behavior when no tasks or occurrences exist
4. **Week view handles empty data correctly**
   - Verifies "Free day" message appears
5. **Month view renders correct data-type attributes**
   - HTML contains `data-type="task"` for tasks
   - HTML contains `data-type="occurrence"` for occurrences
   - HTML does NOT contain `data-type="workout"`
6. **Month view renders correct data-date attributes**
   - Verfies `data-date="2026-03-15"` ISO format is present
7. **Week view renders correct data-type attributes**
   - Same checks as month view for standardized attributes

**Test Configuration**:
```java
@WebMvcTest(CalendarController.class)
class CalendarMonthWeekViewDataDisplayTest {
    
    @Autowired
    private MockMvc mvc;
    
    // Real CalendarDayModelBuilder bean (NOT mocked)
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        CalendarDayModelBuilder calendarDayModelBuilder() {
            return new CalendarDayModelBuilder();
        }
    }
}
```

**Why Real Bean**: The builder contains business logic (populating calendarDays list), so tests use a real instance rather than a mock to verify actual template rendering.

---

## Developer Guide

### Adding New Properties to CalendarDayModel

**Example**: Add a `hasReminders` boolean property

1. **Update CalendarDayModel constructor**:
   ```java
   private final boolean hasReminders;
   
   public CalendarDayModel(LocalDate localDate, LocalDate today, 
                          List<CalendarTask> tasks, 
                          List<ScheduleOccurrence> occurrences) {
       this.localDate = localDate;
       this.isoDate = localDate.toString();
       this.dayOfMonth = localDate.getDayOfMonth();
       this.tasks = tasks != null ? new ArrayList<>(tasks) : new ArrayList<>();
       this.occurrences = occurrences != null ? new ArrayList<>(occurrences) : new ArrayList<>();
       this.status = computeStatus(localDate, today, this.tasks, this.occurrences);
       this.hasReminders = computeHasReminders(this.tasks); // NEW
   }
   
   private boolean computeHasReminders(List<CalendarTask> tasks) {
       return tasks.stream().anyMatch(task -> task.getReminderTime() != null);
   }
   
   public boolean hasReminders() {
       return hasReminders;
   }
   ```

2. **Update CalendarDayModelBuilder** (no changes needed - constructor handles it)

3. **Use in template**:
   ```html
   <div th:each="dayModel : ${calendarDays}">
       <span th:if="${dayModel.hasReminders}" class="reminder-icon">🔔</span>
   </div>
   ```

4. **Add unit tests**:
   ```java
   @Test
   void dayModelHasRemindersWhenTaskHasReminderTime() {
       CalendarTask taskWithReminder = new CalendarTask();
       taskWithReminder.setReminderTime(LocalTime.of(9, 0));
       
       CalendarDayModel day = new CalendarDayModel(
           LocalDate.of(2026, 3, 15), 
           LocalDate.of(2026, 3, 15),
           List.of(taskWithReminder),
           List.of()
       );
       
       assertTrue(day.hasReminders());
   }
   ```

### Maintaining the Builder Pattern

**Do**:
- ✅ Always pre-compute data in `CalendarDayModelBuilder`
- ✅ Use direct property access in templates (`dayModel.tasks`)
- ✅ Add new properties to `CalendarDayModel` constructor
- ✅ Keep defensive copying of mutable collections

**Don't**:
- ❌ Never add map lookups back to templates
- ❌ Don't bypass builder by creating `CalendarDayModel` in controller
- ❌ Avoid mocking `CalendarDayModelBuilder` in MVC tests (use real bean)
- ❌ Don't expose mutable lists from getters without copying

### Test Maintenance

When adding new calendar features:

1. **Add unit tests** to `CalendarDayModelBuilderTest`
   - Test new properties are computed correctly
   - Verify edge cases (null values, empty lists, etc.)

2. **Add integration tests** to `CalendarMonthWeekViewDataDisplayTest`
   - Verify template rendering with new data
   - Check HTML output contains expected attributes

3. **Use `@Bean` in `@TestConfiguration`** for `CalendarDayModelBuilder`
   - Do NOT use `@MockitoBean` (prevents testing actual rendering)
   - Allows tests to catch regressions in data flow

---

## Why This Prevents Regressions

### Type Safety
**Before**: Map keys were `LocalDate`, but template used string keys - **runtime null returns**
**After**: Direct property access - **compile-time safety**, IDE autocomplete catches errors

### Visible Failures
**Before**: Silent null returns in template - empty calendar cells with no error
**After**: Missing properties throw `PropertyNotFoundException` - immediate feedback

### Simplified Testing
**Before**: Hard to test - mocking maps with correct key types was error-prone
**After**: Easy to test - create `CalendarDayModel` objects with known values

### Maintainability
**Before**: Template logic mixed data fetching (map lookups) with presentation
**After**: Clear separation - builder prepares data, template only displays it

---

## Migration Notes

### Backward Compatibility
The refactoring maintains backward compatibility by keeping old attributes:
```java
model.addAttribute("calendarDays", calendarDays);       // NEW
model.addAttribute("tasksByDate", tasksByDate);         // OLD (kept for compatibility)
model.addAttribute("occurrencesByDateIso", occMap);     // OLD (kept for compatibility)
```

If old templates or scripts still reference `tasksByDateIso`, they will continue to work during the transition period.

### Deprecation Path
To fully remove old map-based attributes:
1. Update all templates to use `calendarDays`
2. Search codebase for `tasksByDateIso` and `occurrencesByDateIso` references
3. Remove backward compatibility attributes from controller
4. Run full test suite to catch any remaining usages

---

## Performance Considerations

### Memory Usage
- **Before**: 4 maps per calendar view (tasks, occurrences, iso-keyed variants)
- **After**: 1 list of 28-42 `CalendarDayModel` objects

**Impact**: Slightly higher memory usage per day (stores LocalDate + ISO string), but eliminates 2 duplicate maps. Net reduction in total allocations.

### Processing Time
- **Before**: Template performed map lookups for every day cell (O(n) per day)
- **After**: Builder pre-computes all data once (O(n) total), template accesses pre-built list (O(1) per day)

**Impact**: Faster template rendering, especially for month views with many days.

### Database Queries
No change - controller still makes the same database queries. Refactoring only affects in-memory data structures.

---

## Summary

This refactoring eliminates a critical bug (silent null returns from map key mismatches) by:
1. Introducing type-safe `CalendarDayModel` DTO
2. Pre-computing all day data in `CalendarDayModelBuilder`
3. Simplifying templates to use direct property access
4. Standardizing `data-type` attributes for JavaScript reliability
5. Providing comprehensive tests to prevent future regressions

The new architecture is easier to test, maintain, and extend while providing compile-time safety and clear error messages when issues occur.
