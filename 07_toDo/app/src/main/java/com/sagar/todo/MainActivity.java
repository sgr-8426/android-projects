package com.sagar.todo;

import android.database.Cursor;
import android.database.sqlite.*;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editTextTask;
    Button buttonAdd;
    ListView listViewTasks;
    ArrayList<String> taskList;
    ArrayAdapter<String> adapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextTask = findViewById(R.id.editTextTask);
        buttonAdd = findViewById(R.id.buttonAdd);
        listViewTasks = findViewById(R.id.listViewTasks);

        // Initialize Database
        db = openOrCreateDatabase("TasksDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, task TEXT);");

        // Load existing tasks
        taskList = new ArrayList<>();
        loadTasks();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        listViewTasks.setAdapter(adapter);

        // Add Task Button Click
        buttonAdd.setOnClickListener(v -> {
            String task = editTextTask.getText().toString().trim();
            if (!task.isEmpty()) {
                addTask(task);
                editTextTask.setText("");
            } else {
                Toast.makeText(this, "Enter a task!", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete Task on Click
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            deleteTask(taskList.get(position));
        });
    }

    // Load tasks from database
    private void loadTasks() {
        taskList.clear();
        Cursor cursor = db.rawQuery("SELECT task FROM Tasks", null);
        while (cursor.moveToNext()) {
            taskList.add(cursor.getString(0));
        }
        cursor.close();
    }

    // Add task to database
    private void addTask(String task) {
        String sql = "INSERT INTO Tasks (task) VALUES (?);";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, task);
        statement.executeInsert();

        taskList.add(task);
        adapter.notifyDataSetChanged();
    }

    // Delete task from database
    private void deleteTask(String task) {
        db.execSQL("DELETE FROM Tasks WHERE task = ?", new String[]{task});
        taskList.remove(task);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
    }
}