package ru.practicum.tasktracker.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.exceptions.IntersectDurationTaskException;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;
import ru.practicum.tasktracker.utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskManagerTest <T extends TaskManager>{
    protected T manager;

    @Test
    public void shouldAddTaskAndReturnsTaskById() {
      //  manager.createTask(new Task("name", "descriptions", LocalDateTime.now(), Duration.ofMinutes(10)));
        //manager = (T) Managers.getDefault();
        //Task task = new Task("name", "descriptions", LocalDateTime.now(), Duration.ofMinutes(10));
        //manager.createTask(task);
        Optional<Task> task1 = manager.getTask(1);
        if (task1.isPresent()) {
            Task task2 = task1.get();
            manager.createTask(task2);

            Assertions.assertNotNull(task2, "Задача не добавлена");

            Assertions.assertNotNull(manager.getTask(task2.getId()), "Задача не возвращается по id");
        }
    }
  /*  @Test
    public void shouldAddEpicAndReturnsEpicById(){
        Epic epic = manager.addEpic(new Epic("name", "descriptions"));
        Assertions.assertNotNull(epic, "Epic не добавлен в InMemoryTaskManager");
        Assertions.assertNotNull(manager.getEpic(epic.getId()), "Epic не возвращается по id");
    }

    @Test
    public void shouldAddSubTaskAndReturnsSubTaskById(){
        Epic epic = manager.addEpic(new Epic("name", "descriptions"));
        Subtask subTask = manager.addSubTask(epic.getId(),new Subtask("name", "descriptions",
                LocalDateTime.now(), Duration.ofMinutes(10)));
        Assertions.assertNotNull(subTask, "SubTask не добавлен");
        Assertions.assertNotNull(manager.getSubtask(subTask.getId()), "SubTask не возвращается по id");
    }

    @Test //проверьте, что объект Subtask нельзя сделать своим же эпиком;
    public void shouldReturnNullIfEpicIdEqualsSubTaskId() {
        Subtask subTask = new Subtask("name", "descriptions", LocalDateTime.now(), Duration.ofMinutes(10));
        subTask.setId(1);
        Subtask addedSubTask = manager.addSubTask(1, subTask);
        Assertions.assertNull(addedSubTask,"В список подзадач занесена подзадача, у которой" +
                "id занесено в поле для ее epicId");
    }

    @Test
    public void shouldEqualZeroIfRemoveSubTask(){
        Epic epic = manager.addEpic(new Epic("name1", "descriptions1"));
        Subtask subTask = manager.addSubTask(epic.getId(), new Subtask("name2", "descriptions2",
                LocalDateTime.now(), Duration.ofMinutes(10)));

        Assertions.assertTrue(epic.getSubTasksId().contains(subTask.getId()), "Подзадачи нет в списке подзадач эпика");

        manager.removeSubTask(subTask.getId());

        Assertions.assertEquals(0,epic.getSubTasksId().size(), "Подзадача осталась в списке подзадач эпика" +
                "после удаления");
    }

    @Test
    public void shouldThrowExceptionThenAddTaskWithIntersectionDuration(){
        Task task1 = manager.addTask(new Task("name1", "descriptions1", LocalDateTime.now(), Duration.ofMinutes(10)));
        Task task2 = new Task("name2", "descriptions2", LocalDateTime.now(), Duration.ofMinutes(10));
        Task task3 = new Task("name2", "descriptions2", LocalDateTime.now().plusMinutes(5),
                Duration.ofMinutes(10));

        Assertions.assertThrows(IntersectDurationTaskException.class, () -> manager.addTask(task2),
                "При добавлении задачи начинающейся в тоже время, что и предыдущая не выбрасывается исключение");
        Assertions.assertThrows(IntersectDurationTaskException.class, () -> manager.addTask(task3),
                "При добавлении задачи с пересечением по времени выполнения не выбрасывается исключение");
    }


    @Test
    public void shouldBePrioritized(){
        Task task1 = manager.addTask(new Task("name1", "descriptions1", LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(10)));
        Task task2 = manager.addTask(new Task("name2", "descriptions2", LocalDateTime.now(), Duration.ofMinutes(10)));
        Task task3 = manager.addTask(new Task("name2", "descriptions2", LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10)));

        Assertions.assertEquals(task2, manager.getPrioritizedTasks().get(0), "Задача с более высоким приоритетом не на 0 индексе");
        Assertions.assertEquals(task1, manager.getPrioritizedTasks().get(1), "Задача не на 1 индексе");
        Assertions.assertEquals(task3, manager.getPrioritizedTasks().get(2), "Задача с более низким приоритетом не на 2 индексе");
        manager.removeTask(task2.getId());
        Assertions.assertEquals(task1, manager.getPrioritizedTasks().get(0),"После удаления задачи с самым высоким приоритетом" +
                "следующая задача не на 0 индексе");
    }

    @Test
    public void startTimeAndDurationEpicShouldBeCalculatedBySubTasks(){
        Epic epic = manager.addEpic(new Epic("name1", "descriptions1"));
        Subtask subTask1 = manager.addSubTask(epic.getId(), new Subtask("name2", "descriptions2",
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10)));
        Subtask subTask2 = manager.addSubTask(epic.getId(), new Subtask("name3", "descriptions3",
                LocalDateTime.now(), Duration.ofMinutes(10)));

        Assertions.assertEquals(manager.getSubtask(subTask2.getId()).getStartTime(), manager.getEpic(epic.getId()).getStartTime(),
                "Время начала Эпика не равно самому раннему времени начала подзадачи");
        Assertions.assertEquals((manager.getSubtask(subTask1.getId()).getDuration().toMinutes()
                        + manager.getSubtask(subTask2.getId()).getDuration().toMinutes()),
                manager.getEpic(epic.getId()).getDuration().toMinutes(),
                "Продолжительность Эпика не равно сумме продолжительности подзадач");
    }

    @Test
    public void taskStatusEpicShouldBeCalculatedBySubTasks(){
        Epic epic = manager.addEpic(new Epic("name1", "descriptions1"));
        Subtask subTask1 = manager.addSubTask(epic.getId(), new Subtask("name2", "descriptions2",
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10)));
        Subtask subTask2 = manager.addSubTask(epic.getId(), new Subtask("name3", "descriptions3",
                LocalDateTime.now(), Duration.ofMinutes(10)));

        Assertions.assertTrue(epic.getStatus() == Status.NEW, "Статус epic не NEW, когда у двух" +
                "subtask статус NEW");

        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask1);
        Assertions.assertTrue(epic.getStatus() == Status.IN_PROGRESS,"Статус epic не IN_PROGRESS," +
                " когда у оной из двух subtask статус IN_PROGRESS");

        subTask1.setStatus(Status.DONE);
        manager.updateSubTask(subTask1);
        subTask2.setStatus(Status.DONE);
        manager.updateSubTask(subTask2);
        Assertions.assertTrue(epic.getStatus() == Status.DONE, "Статус epic не DONE, когда у двух" +
                "subtask статус DONE");
    }*/
}