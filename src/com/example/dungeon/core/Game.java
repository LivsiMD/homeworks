package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Game {
    private final GameState state = new GameState();
    private final Map<String, Command> commands = new LinkedHashMap<>();

    static {
        WorldInfo.touch("Game");
    }

    public Game() {
        registerCommands();
        bootstrapWorld();
    }

    private void registerCommands() {
        commands.put("help", (ctx, a) -> System.out.println("Команды: " + String.join(", ", commands.keySet())));
        commands.put("gc-stats", (ctx, a) -> {
            Runtime rt = Runtime.getRuntime();
            long free = rt.freeMemory(), total = rt.totalMemory(), used = total - free;
            System.out.println("Память: used=" + used + " free=" + free + " total=" + total);
        });
        commands.put("look", (ctx, a) -> System.out.println(ctx.getCurrent().describe()));

// Команда: перемещение игрока
        commands.put("move", (ctx, a) -> {
            if (a.size() < 1) {
                throw new InvalidCommandException("Использование: move <north|south|east|west>");
            }
            String dir = a.get(0).toLowerCase();  // теперь берём первый элемент после команды
            Room current = ctx.getCurrent();
            Room next = current.getNeighbors().get(dir);

            if (next == null) {
                throw new InvalidCommandException("Нет выхода в направлении: " + dir);
            }

            ctx.setCurrent(next);
            System.out.println("Вы перешли в: " + next.getName());
            System.out.println(next.describe());
        });


// Команда: взять предмет
        commands.put("take", (ctx, a) -> {
            if (a == null || a.size() < 1) {
                throw new InvalidCommandException("Использование: take <item name>");
            }

            // Собираем всё, что пришло как аргументы, в одно имя предмета
            String itemName = String.join(" ", a).trim();

            Room room = ctx.getCurrent();

            // Сначала точное совпадение по имени (case-insensitive)
            Optional<Item> found = room.getItems().stream()
                    .filter(i -> i.getName().equalsIgnoreCase(itemName))
                    .findFirst();

            // Если точного совпадения нет — пробуем contains (нечёткий поиск)
            if (found.isEmpty()) {
                String q = itemName.toLowerCase();
                found = room.getItems().stream()
                        .filter(i -> i.getName().toLowerCase().contains(q))
                        .findFirst();
            }

            if (found.isEmpty()) {
                throw new InvalidCommandException("Предмет не найден: " + itemName);
            }

            Item item = found.get();
            ctx.getPlayer().getInventory().add(item);
            room.getItems().remove(item);
            System.out.println("Взято: " + item.getName());
        });

// Команда: инвентарь (с использованием Stream API)
        commands.put("inventory", (ctx, a) -> {
            List<Item> inv = ctx.getPlayer().getInventory();
            if (inv.isEmpty()) {
                System.out.println("Инвентарь пуст.");
                return;
            }

            inv.stream()
                    .collect(java.util.stream.Collectors.groupingBy(i -> i.getClass().getSimpleName()))
                    .forEach((type, items) -> {
                        System.out.println("- " + type + " (" + items.size() + "): " +
                                items.stream().map(Item::getName).toList());
                    });
        });

// Команда: использовать предмет
        commands.put("use", (ctx, a) -> {
            if (a == null || a.size() < 1) {
                throw new InvalidCommandException("Использование: use <item name>");
            }

            String itemName = String.join(" ", a).trim();

            Optional<Item> found = ctx.getPlayer().getInventory().stream()
                    .filter(i -> i.getName().equalsIgnoreCase(itemName))
                    .findFirst();

            if (found.isEmpty()) {
                String q = itemName.toLowerCase();
                found = ctx.getPlayer().getInventory().stream()
                        .filter(i -> i.getName().toLowerCase().contains(q))
                        .findFirst();
            }

            if (found.isEmpty()) {
                throw new InvalidCommandException("У вас нет предмета: " + itemName);
            }

            Item item = found.get();
            item.apply(ctx); // передаём GameState
            ctx.getPlayer().getInventory().remove(item);
            System.out.println("Использован предмет: " + item.getName());
        });

// Команда: бой с монстром
        commands.put("fight", (ctx, a) -> {
            Room room = ctx.getCurrent();
            Monster monster = room.getMonster();
            Player player = ctx.getPlayer();

            if (monster == null) {
                throw new InvalidCommandException("В этой комнате нет монстра.");
            }

            while (player.getHp() > 0 && monster.getHp() > 0) {
                // атака игрока
                int dmg = player.getAttack();
                monster.setHp(monster.getHp() - dmg);
                System.out.println("Вы бьёте " + monster.getName() + " на " + dmg +
                        ". HP монстра: " + Math.max(0, monster.getHp()));

                if (monster.getHp() <= 0) {
                    System.out.println("Монстр повержен!");
                    room.setMonster(null);

                    // Выпадение фиксированного лута (например, зелье)
                    Item loot = new Potion("Зелье лечения", 5);
                    room.getItems().add(loot);
                    System.out.println("Монстр уронил: " + loot.getName());

                    return;
                }

                // ответ монстра (пусть сила атаки зависит от уровня)
                int mdmg = monster.getLevel();
                player.setHp(player.getHp() - mdmg);
                System.out.println("Монстр отвечает на " + mdmg +
                        ". Ваше HP: " + Math.max(0, player.getHp()));

                if (player.getHp() <= 0) {
                    System.out.println("Вы погибли... Игра окончена.");
                    System.exit(0);
                }
            }
        });

        // Сохранение игры
        commands.put("save", (ctx, a) -> {
            try {
                SaveLoad.save(ctx); // сохраняем текущее состояние
            } catch (Exception e) {
                throw new InvalidCommandException("Ошибка сохранения: " + e.getMessage());
            }
        });

// Загрузка игры
        commands.put("load", (ctx, a) -> {
            try {
                SaveLoad.load(ctx);
            } catch (IndexOutOfBoundsException e) {
                // fallback: читаем save.txt вручную
                try (Scanner scanner = new Scanner(new java.io.File("save.txt"))) {
                    Map<String, String> map = new HashMap<>();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(";", 2);
                        if (parts.length == 2) {
                            map.put(parts[0], parts[1]);
                        }
                    }
                    Player p = ctx.getPlayer();
                    String playerLine = map.get("player");
                    if (playerLine != null) {
                        String[] pp = playerLine.split(";");
                        if (pp.length >= 3) {
                            p.setName(pp[0]);
                            p.setHp(Integer.parseInt(pp[1]));
                            p.setAttack(Integer.parseInt(pp[2]));
                        }
                    }
                    System.out.println("Игра загружена (fallback).");
                } catch (Exception ex) {
                    throw new InvalidCommandException("Ошибка загрузки (fallback): " + ex.getMessage());
                }
            } catch (Exception e) {
                throw new InvalidCommandException("Ошибка загрузки: " + e.getMessage());
            }
        });
// Таблица лидеров
        commands.put("scores", (ctx, a) -> {
            SaveLoad.printScores();
        });


// Информация об игре
        commands.put("about", (ctx, a) -> {
            System.out.println("DungeonMini — Промежуточная аттестация");
            System.out.println("Автор: Демин Л.В. Направление аналитики");
            System.out.println("Версия: 1.0");
            System.out.println("Цель: на практике применить изученные темы курса");
        });
    }

    private void bootstrapWorld() {
        Player hero = new Player("Герой", 20, 5);
        state.setPlayer(hero);

        Room square = new Room("Площадь", "Каменная площадь с фонтаном.");
        Room forest = new Room("Лес", "Шелест листвы и птичий щебет.");
        Room cave = new Room("Пещера", "Темно и сыро.");
        square.getNeighbors().put("north", forest);
        forest.getNeighbors().put("south", square);
        forest.getNeighbors().put("east", cave);
        cave.getNeighbors().put("west", forest);

        forest.getItems().add(new Potion("Малое зелье", 5));
        forest.setMonster(new Monster("Волк", 1, 8));

        state.setCurrent(square);
    }

    public void run() {
        System.out.println("DungeonMini (TEMPLATE). 'help' — команды.");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("> ");
                String line = in.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> parts = Arrays.asList(line.split("\s+"));
                String cmd = parts.getFirst().toLowerCase(Locale.ROOT);
                List<String> args = parts.subList(1, parts.size());
                Command c = commands.get(cmd);
                try {
                    if (c == null) throw new InvalidCommandException("Неизвестная команда: " + cmd);
                    c.execute(state, args);
                    state.addScore(1);
                } catch (InvalidCommandException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Непредвиденная ошибка: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода: " + e.getMessage());
        }
    }
}

// -------------------------------------------------------------
// Примеры ошибок для задания:

// Ошибка компиляции (пример):
// int x = "строка";
// ↑ типы несовместимы, такой код даже не скомпилируется.

// Другой вариант компиляционной ошибки:
// System.out.println(notExistVar);
// ↑ переменная notExistVar нигде не объявлена, компилятор остановит сборку.

// Ошибка выполнения (пример):
// int a = 10;
// int b = 0;
// int c = a / b;
// ↑ программа скомпилируется, но при запуске
// упадёт с ArithmeticException: / by zero
// -------------------------------------------------------------