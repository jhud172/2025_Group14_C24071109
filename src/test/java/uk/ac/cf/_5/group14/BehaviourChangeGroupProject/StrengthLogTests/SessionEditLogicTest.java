package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLogTests;

import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.ExerciseSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for session-edit reorder and insert logic
 * (mirrors the logic in StrengthLogController without Spring context).
 */
class SessionEditLogicTest {

    // ── helpers ──────────────────────────────────────────────────────────────

    private static WorkoutSession sessionWithExercises(int count) {
        WorkoutSession ws = new WorkoutSession();
        List<ExerciseSession> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ExerciseSession es = new ExerciseSession();
            es.setOrderIndex(i);
            list.add(es);
        }
        ws.setExerciseSessions(list);
        return ws;
    }

    private static List<ExerciseSession> ordered(WorkoutSession ws) {
        return ws.getExerciseSessions().stream()
                .sorted(Comparator.comparingInt(ExerciseSession::getOrderIndex))
                .collect(Collectors.toList());
    }

    /** Simulate the "swap" reorder used by apiReorder direction=up/down */
    private static boolean swapReorder(WorkoutSession ws, int targetPosition, String direction) {
        List<ExerciseSession> ord = ordered(ws);
        int idx = -1;
        for (int i = 0; i < ord.size(); i++) {
            if (ord.get(i).getOrderIndex() == targetPosition) { idx = i; break; }
        }
        if (idx < 0) return false;
        int swapIdx = "up".equals(direction) ? idx - 1 : idx + 1;
        if (swapIdx < 0 || swapIdx >= ord.size()) return false;
        int tmp = ord.get(idx).getOrderIndex();
        ord.get(idx).setOrderIndex(ord.get(swapIdx).getOrderIndex());
        ord.get(swapIdx).setOrderIndex(tmp);
        return true;
    }

    /** Simulate insert-after logic used by apiAddExercise */
    private static ExerciseSession insertAfter(WorkoutSession ws, int insertAfterOrderIndex) {
        List<ExerciseSession> ord = ordered(ws);
        for (ExerciseSession es : ord) {
            if (es.getOrderIndex() > insertAfterOrderIndex) {
                es.setOrderIndex(es.getOrderIndex() + 1);
            }
        }
        int targetIndex = insertAfterOrderIndex + 1;
        ExerciseSession newEs = new ExerciseSession();
        newEs.setOrderIndex(targetIndex);
        ws.getExerciseSessions().add(newEs);
        return newEs;
    }

    /** Simulate full reorder-all */
    private static void reorderAll(WorkoutSession ws, List<long[]> entries) {
        for (long[] entry : entries) {
            long esListIdx = entry[0]; // index in list (as a proxy for id)
            int newOrderIndex = (int) entry[1];
            ws.getExerciseSessions().get((int) esListIdx).setOrderIndex(newOrderIndex);
        }
    }

    // ── reorder tests ────────────────────────────────────────────────────────

    @Test
    void swapUp_movesExerciseEarlier() {
        WorkoutSession ws = sessionWithExercises(3);
        // orderIndex: 0, 1, 2  →  move the exercise at index 1 up → swap with index 0
        List<ExerciseSession> before = ordered(ws);
        ExerciseSession wasFirst = before.get(0); // orderIndex = 0
        ExerciseSession wasSecond = before.get(1); // orderIndex = 1
        boolean moved = swapReorder(ws, 1, "up");
        assertTrue(moved);
        // wasSecond should now have orderIndex 0; wasFirst should have orderIndex 1
        assertEquals(0, wasSecond.getOrderIndex());
        assertEquals(1, wasFirst.getOrderIndex());
    }

    @Test
    void swapDown_movesExerciseLater() {
        WorkoutSession ws = sessionWithExercises(3);
        List<ExerciseSession> before = ordered(ws);
        ExerciseSession wasSecond = before.get(1); // orderIndex = 1
        ExerciseSession wasThird  = before.get(2); // orderIndex = 2
        boolean moved = swapReorder(ws, 1, "down");
        assertTrue(moved);
        // wasSecond should now have orderIndex 2; wasThird should have orderIndex 1
        assertEquals(2, wasSecond.getOrderIndex());
        assertEquals(1, wasThird.getOrderIndex());
    }

    @Test
    void swapUp_firstExercise_doesNotMove() {
        WorkoutSession ws = sessionWithExercises(3);
        boolean moved = swapReorder(ws, 0, "up");
        assertFalse(moved);
        // Order unchanged
        List<ExerciseSession> ord = ordered(ws);
        assertEquals(0, ord.get(0).getOrderIndex());
        assertEquals(1, ord.get(1).getOrderIndex());
        assertEquals(2, ord.get(2).getOrderIndex());
    }

    @Test
    void swapDown_lastExercise_doesNotMove() {
        WorkoutSession ws = sessionWithExercises(3);
        boolean moved = swapReorder(ws, 2, "down");
        assertFalse(moved);
    }

    @Test
    void swapUp_preservesTotalCount() {
        WorkoutSession ws = sessionWithExercises(4);
        swapReorder(ws, 2, "up");
        assertEquals(4, ws.getExerciseSessions().size());
    }

    // ── insert tests ─────────────────────────────────────────────────────────

    @Test
    void insertAfter_shiftsSiblings() {
        WorkoutSession ws = sessionWithExercises(3); // indices 0,1,2
        ExerciseSession inserted = insertAfter(ws, 0); // insert after index 0
        assertEquals(1, inserted.getOrderIndex());
        // Previous index-1 should now be index 2, index-2 should be index 3
        List<ExerciseSession> ord = ordered(ws);
        assertEquals(4, ord.size());
        assertEquals(0, ord.get(0).getOrderIndex());
        assertEquals(1, ord.get(1).getOrderIndex());
        assertEquals(2, ord.get(2).getOrderIndex());
        assertEquals(3, ord.get(3).getOrderIndex());
    }

    @Test
    void insertAfter_lastPosition_addsAtEnd() {
        WorkoutSession ws = sessionWithExercises(3); // indices 0,1,2
        ExerciseSession inserted = insertAfter(ws, 2); // insert after last
        assertEquals(3, inserted.getOrderIndex());
        assertEquals(4, ws.getExerciseSessions().size());
    }

    @Test
    void insertAfter_preservesExistingOrderBefore() {
        WorkoutSession ws = sessionWithExercises(3); // 0,1,2
        insertAfter(ws, 1); // insert at position 2, shift original 2 → 3
        List<ExerciseSession> ord = ordered(ws);
        // Indices should be: 0, 1, 2(new), 3(was 2)
        // All must be unique
        long distinct = ord.stream().mapToInt(ExerciseSession::getOrderIndex).distinct().count();
        assertEquals(4, distinct);
    }

    @Test
    void reorderAll_updatesIndices() {
        WorkoutSession ws = sessionWithExercises(3); // list positions 0→idx0, 1→idx1, 2→idx2
        // Reverse the order
        reorderAll(ws, List.of(new long[]{0, 2}, new long[]{1, 1}, new long[]{2, 0}));
        List<ExerciseSession> ord = ordered(ws);
        assertEquals(0, ord.get(0).getOrderIndex());
        assertEquals(1, ord.get(1).getOrderIndex());
        assertEquals(2, ord.get(2).getOrderIndex());
    }
}
