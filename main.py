"""Habit Mountain - a small, local-first Kivy Android app."""

from dataclasses import dataclass
from typing import Callable, Optional

from kivy.app import App
from kivy.core.window import Window
from kivy.graphics import Color, Ellipse, Line, Mesh, Rectangle, RoundedRectangle
from kivy.metrics import dp, sp
from kivy.properties import ListProperty, NumericProperty
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.uix.popup import Popup
from kivy.uix.scrollview import ScrollView
from kivy.uix.spinner import Spinner
from kivy.uix.textinput import TextInput
from kivy.uix.widget import Widget


Window.clearcolor = (0.055, 0.094, 0.141, 1)


def color(hex_value: str, alpha: float = 1.0) -> tuple[float, float, float, float]:
    value = hex_value.lstrip("#")
    return tuple(int(value[index : index + 2], 16) / 255 for index in (0, 2, 4)) + (alpha,)


INK = color("#0E1824")
INK_SOFT = color("#172536")
SNOW = color("#F7F5EF")
MIST = color("#A8B7C6")
TEAL = color("#55C6A9")
GOLD = color("#F1B45B")
SKY = color("#233A4B")


@dataclass
class Habit:
    habit_id: int
    name: str
    cadence: str
    interval_days: Optional[int] = None
    reward: int = 10
    streak: int = 0
    completed: bool = False
    accent: tuple[float, float, float, float] = TEAL

    @property
    def schedule_label(self) -> str:
        if self.cadence == "Custom":
            return f"Every {self.interval_days or 1} days"
        return self.cadence


class HabitRepository:
    """Small in-memory data layer; replace with SQLite/JSON persistence later."""

    def __init__(self) -> None:
        self._next_id = 4
        self.coins = 128
        self.habits = [
            Habit(1, "Drink water", "Daily", reward=12, streak=4, completed=True, accent=color("#59C6DE")),
            Habit(2, "Read 20 pages", "Daily", reward=15, streak=3, accent=GOLD),
            Habit(3, "Morning run", "Weekly", reward=25, streak=8, accent=color("#CB7FEC")),
        ]

    @property
    def completed_today(self) -> int:
        return sum(habit.completed for habit in self.habits)

    def toggle(self, habit_id: int) -> None:
        for habit in self.habits:
            if habit.habit_id != habit_id:
                continue
            if habit.completed:
                self.coins = max(0, self.coins - habit.reward)
                habit.completed = False
                habit.streak = max(0, habit.streak - 1)
            else:
                self.coins += habit.reward
                habit.completed = True
                habit.streak += 1
            return

    def add(self, name: str, cadence: str, interval_days: Optional[int]) -> None:
        self.habits.append(
            Habit(
                self._next_id,
                name.strip(),
                cadence,
                interval_days=interval_days if cadence == "Custom" else None,
                accent=TEAL,
            )
        )
        self._next_id += 1


class Card(BoxLayout):
    background = ListProperty(INK_SOFT)

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        with self.canvas.before:
            self._background_color = Color(*self.background)
            self._background = RoundedRectangle(pos=self.pos, size=self.size, radius=[dp(18)] * 4)
        self.bind(pos=self._sync_background, size=self._sync_background, background=self._sync_background)

    def _sync_background(self, *_args):
        self._background.pos = self.pos
        self._background.size = self.size
        self._background_color.rgba = self.background


class RoundedButton(Button):
    fill = ListProperty(TEAL)
    pressed_fill = ListProperty(color("#3FA188"))

    def __init__(self, **kwargs):
        kwargs.setdefault("background_color", (0, 0, 0, 0))
        kwargs.setdefault("color", INK)
        kwargs.setdefault("font_size", sp(14))
        super().__init__(**kwargs)
        with self.canvas.before:
            self._fill_color = Color(*self.fill)
            self._fill = RoundedRectangle(pos=self.pos, size=self.size, radius=[dp(14)] * 4)
        self.bind(pos=self._sync_fill, size=self._sync_fill, state=self._sync_fill, fill=self._sync_fill)

    def _sync_fill(self, *_args):
        self._fill.pos = self.pos
        self._fill.size = self.size
        self._fill_color.rgba = self.pressed_fill if self.state == "down" else self.fill


class MountainPanel(Widget):
    progress = NumericProperty(0.0)

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.bind(pos=self.redraw, size=self.redraw, progress=self.redraw)

    def redraw(self, *_args):
        self.canvas.clear()
        x, y, width, height = self.x, self.y, self.width, self.height
        p = min(1.0, max(0.0, self.progress))
        with self.canvas:
            Color(*SKY)
            Rectangle(pos=(x, y), size=(width, height))
            Color(*color("#F1B45B", 0.18))
            Ellipse(pos=(x + width * 0.70, y + height * 0.57), size=(dp(70), dp(70)))
            Color(*color("#263F4E"))
            self._mountain_mesh(self._mountain_points(x, y, width, height, 0.82, 0.02))
            Color(*color("#1C3241"))
            self._mountain_mesh(self._mountain_points(x, y, width, height, 0.69, 0.14))
            Color(*color("#122733"))
            self._mountain_mesh(self._mountain_points(x, y, width, height, 0.59, 0.23))

            trail = [
                x + width * 0.14, y + height * 0.12,
                x + width * 0.34, y + height * 0.27,
                x + width * 0.44, y + height * 0.40,
                x + width * 0.57, y + height * 0.56,
                x + width * 0.74, y + height * 0.75,
            ]
            Color(*color("#F1B45B", 0.48))
            Line(points=trail, width=dp(2.2))

            marker_x = x + width * (0.14 + p * 0.60)
            marker_y = y + height * (0.14 + p * 0.61)
            Color(*TEAL)
            Ellipse(pos=(marker_x - dp(10), marker_y - dp(10)), size=(dp(20), dp(20)))
            Color(*SNOW)
            Ellipse(pos=(marker_x - dp(4), marker_y - dp(5)), size=(dp(8), dp(8)))
            Color(*TEAL)
            Line(points=[marker_x, marker_y - dp(10), marker_x, marker_y - dp(23)], width=dp(2.4))

    @staticmethod
    def _mountain_mesh(points):
        vertices = []
        for index in range(0, len(points), 2):
            vertices.extend((points[index], points[index + 1], 0, 0))
        return Mesh(vertices=vertices, indices=list(range(len(vertices) // 4)), mode="triangle_fan")

    @staticmethod
    def _mountain_points(x, y, width, height, peak_height, left_offset):
        return [
            x, y, x + width * left_offset, y + height * 0.17,
            x + width * 0.45, y + height * peak_height,
            x + width * 0.62, y + height * 0.33,
            x + width * 0.86, y + height * 0.12,
            x + width, y + height * 0.32, x + width, y,
        ]


class HabitCard(Card):
    def __init__(self, habit: Habit, on_toggle: Callable[[int], None], **kwargs):
        super().__init__(orientation="horizontal", padding=(dp(14), dp(11)), spacing=dp(12), **kwargs)

        icon = Card(
            orientation="vertical", size_hint=(None, None), size=(dp(42), dp(42)), padding=0,
            background=habit.accent[:3] + (0.18,),
        )
        icon.add_widget(Label(text="◆", color=habit.accent, font_size=sp(15), bold=True))
        self.add_widget(icon)

        copy = BoxLayout(orientation="vertical", spacing=dp(1))
        copy.add_widget(Label(text=habit.name, color=SNOW, font_size=sp(16), bold=True, halign="left"))
        copy.add_widget(Label(text=f"{habit.schedule_label}  ·  {habit.streak} day streak", color=habit.accent, font_size=sp(12), halign="left"))
        self.add_widget(copy)

        check = RoundedButton(
            text="✓" if habit.completed else "›",
            fill=TEAL if habit.completed else color("#2A3A4A"),
            color=INK if habit.completed else SNOW,
            font_size=sp(20), size_hint=(None, None), size=(dp(38), dp(38)),
        )
        check.bind(on_release=lambda *_: on_toggle(habit.habit_id))
        self.add_widget(check)


class AddHabitPopup(Popup):
    def __init__(self, on_add: Callable[[str, str, Optional[int]], None], **kwargs):
        super().__init__(title="New habit", size_hint=(0.92, 0.66), separator_color=TEAL, **kwargs)
        self.on_add_callback = on_add
        content = BoxLayout(orientation="vertical", padding=dp(18), spacing=dp(12))
        content.add_widget(Label(text="Add a step to your trail.", color=MIST, font_size=sp(13), size_hint_y=None, height=dp(24)))
        self.name_input = TextInput(hint_text="Habit name", multiline=False, size_hint_y=None, height=dp(48), padding=(dp(12), dp(12)))
        content.add_widget(self.name_input)
        self.cadence_spinner = Spinner(text="Daily", values=("Daily", "Weekly", "Custom"), size_hint_y=None, height=dp(48))
        self.cadence_spinner.bind(text=self._cadence_changed)
        content.add_widget(self.cadence_spinner)
        self.interval_input = TextInput(hint_text="Repeat every (days)", input_filter="int", multiline=False, size_hint_y=None, height=dp(48), opacity=0, disabled=True)
        content.add_widget(self.interval_input)

        buttons = BoxLayout(size_hint_y=None, height=dp(48), spacing=dp(10))
        cancel = RoundedButton(text="Cancel", fill=color("#2A3A4A"), color=SNOW)
        cancel.bind(on_release=self.dismiss)
        add = RoundedButton(text="Add to trail", fill=TEAL, color=INK)
        add.bind(on_release=self._submit)
        buttons.add_widget(cancel)
        buttons.add_widget(add)
        content.add_widget(buttons)
        self.content = content

    def _cadence_changed(self, _spinner, value):
        is_custom = value == "Custom"
        self.interval_input.opacity = 1 if is_custom else 0
        self.interval_input.disabled = not is_custom

    def _submit(self, *_args):
        name = self.name_input.text.strip()
        cadence = self.cadence_spinner.text
        interval = int(self.interval_input.text) if self.interval_input.text else 2
        if name and (cadence != "Custom" or interval > 0):
            self.on_add_callback(name, cadence, interval if cadence == "Custom" else None)
            self.dismiss()


class HabitMountainApp(App):
    title = "Habit Mountain"

    def build(self):
        self.repository = HabitRepository()
        self.root_layout = BoxLayout(orientation="vertical", padding=(dp(20), dp(10), dp(20), dp(12)), spacing=dp(12))
        self._build_header()
        self._build_mountain()
        self._build_habit_list()
        self._build_add_button()
        self.refresh()
        return self.root_layout

    def _build_header(self):
        header = BoxLayout(size_hint_y=None, height=dp(70), spacing=dp(12))
        title = BoxLayout(orientation="vertical")
        title.add_widget(Label(text="H A B I T   M O U N T A I N", color=MIST, font_size=sp(11), bold=True, halign="left"))
        title.add_widget(Label(text="Keep climbing.", color=SNOW, font_size=sp(26), bold=True, halign="left"))
        header.add_widget(title)
        self.coins_card = Card(orientation="horizontal", size_hint=(None, None), size=(dp(88), dp(40)), padding=(dp(10), 0), spacing=dp(5), background=INK_SOFT)
        self.coins_card.add_widget(Label(text="◆", color=GOLD, font_size=sp(14), size_hint_x=None, width=dp(16)))
        self.coins_label = Label(text="0", color=SNOW, font_size=sp(15), bold=True)
        self.coins_card.add_widget(self.coins_label)
        header.add_widget(self.coins_card)
        self.root_layout.add_widget(header)

    def _build_mountain(self):
        mountain_card = Card(orientation="vertical", padding=0, spacing=0, size_hint_y=None, height=dp(284), background=INK_SOFT)
        overlay = Widget()
        self.mountain = MountainPanel()
        overlay.add_widget(self.mountain)
        labels = BoxLayout(orientation="vertical", padding=(dp(18), dp(16)), size_hint=(1, 1))
        labels.add_widget(Label(text="BASE CAMP", color=GOLD, font_size=sp(11), bold=True, halign="left", valign="top"))
        labels.add_widget(Label(text="Summit trail", color=SNOW, font_size=sp(22), bold=True, halign="left", valign="top"))
        labels.add_widget(Label(text="Level 1", color=MIST, font_size=sp(13), halign="left", valign="top"))
        labels.add_widget(Widget())
        overlay.add_widget(labels)
        mountain_card.add_widget(overlay)

        progress_row = BoxLayout(size_hint_y=None, height=dp(46), padding=(dp(18), dp(10), dp(18), dp(10)))
        self.progress_label = Label(text="Trail progress", color=MIST, font_size=sp(12), halign="left")
        progress_row.add_widget(self.progress_label)
        self.meters_label = Label(text="0 m climbed", color=TEAL, font_size=sp(12), bold=True, halign="right")
        progress_row.add_widget(self.meters_label)
        mountain_card.add_widget(progress_row)
        self.root_layout.add_widget(mountain_card)

    def _build_habit_list(self):
        heading = BoxLayout(size_hint_y=None, height=dp(34))
        heading.add_widget(Label(text="Today's climb", color=SNOW, font_size=sp(20), bold=True, halign="left"))
        self.complete_label = Label(text="0/0 complete", color=TEAL, font_size=sp(12), halign="right", valign="middle")
        heading.add_widget(self.complete_label)
        self.root_layout.add_widget(heading)

        self.scroll = ScrollView(do_scroll_x=False, bar_width=0)
        self.habit_list = BoxLayout(orientation="vertical", spacing=dp(10), size_hint_y=None)
        self.habit_list.bind(minimum_height=self.habit_list.setter("height"))
        self.scroll.add_widget(self.habit_list)
        self.root_layout.add_widget(self.scroll)

    def _build_add_button(self):
        add_button = RoundedButton(text="＋  Add a habit", size_hint_y=None, height=dp(52), fill=TEAL, color=INK, font_size=sp(15))
        add_button.bind(on_release=self._open_add_popup)
        self.root_layout.add_widget(add_button)

    def _open_add_popup(self, *_args):
        AddHabitPopup(on_add=self._add_habit).open()

    def _add_habit(self, name: str, cadence: str, interval_days: Optional[int]):
        self.repository.add(name, cadence, interval_days)
        self.refresh()

    def _toggle_habit(self, habit_id: int):
        self.repository.toggle(habit_id)
        self.refresh()

    def refresh(self):
        if not hasattr(self, "habit_list"):
            return
        completed = self.repository.completed_today
        total = len(self.repository.habits)
        progress = completed / total if total else 0
        self.coins_label.text = str(self.repository.coins)
        self.complete_label.text = f"{completed}/{total} complete"
        self.meters_label.text = f"{completed * 10} m climbed"
        self.mountain.progress = progress
        self.habit_list.clear_widgets()
        for habit in self.repository.habits:
            self.habit_list.add_widget(HabitCard(habit, self._toggle_habit, size_hint_y=None, height=dp(66)))


if __name__ == "__main__":
    HabitMountainApp().run()
