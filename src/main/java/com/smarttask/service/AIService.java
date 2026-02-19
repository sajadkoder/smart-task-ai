package com.smarttask.service;

import com.smarttask.entity.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIService {

    // Keyword mappings for categorization
    private static final Map<String, Task.TaskCategory> CATEGORY_KEYWORDS = new HashMap<>();
    
    static {
        // Work-related keywords
        CATEGORY_KEYWORDS.put("meeting", Task.TaskCategory.WORK);
        CATEGORY_KEYWORDS.put("project", Task.TaskCategory.WORK);
        CATEGORY_KEYWORDS.put("deadline", Task.TaskCategory.WORK);
        CATEGORY_KEYWORDS.put("client", Task.TaskCategory.WORK);
        CATEGORY_KEYWORDS.put("presentation", Task.TaskCategory.WORK);
        CATEGORY_KEYWORDS.put("report", Task.TaskCategory.WORK);
        CATEGORY_KEYWORDS.put("email", Task.TaskCategory.WORK);
        CATEGORY_KEYWORDS.put("office", Task.TaskCategory.WORK);
        
        // Health-related keywords
        CATEGORY_KEYWORDS.put("exercise", Task.TaskCategory.HEALTH);
        CATEGORY_KEYWORDS.put("workout", Task.TaskCategory.HEALTH);
        CATEGORY_KEYWORDS.put("gym", Task.TaskCategory.HEALTH);
        CATEGORY_KEYWORDS.put("doctor", Task.TaskCategory.HEALTH);
        CATEGORY_KEYWORDS.put("medicine", Task.TaskCategory.HEALTH);
        CATEGORY_KEYWORDS.put("appointment", Task.TaskCategory.HEALTH);
        CATEGORY_KEYWORDS.put("health", Task.TaskCategory.HEALTH);
        
        // Personal-related keywords
        CATEGORY_KEYWORDS.put("birthday", Task.TaskCategory.PERSONAL);
        CATEGORY_KEYWORDS.put("family", Task.TaskCategory.PERSONAL);
        CATEGORY_KEYWORDS.put("friend", Task.TaskCategory.PERSONAL);
        CATEGORY_KEYWORDS.put("home", Task.TaskCategory.PERSONAL);
        
        // Learning-related keywords
        CATEGORY_KEYWORDS.put("study", Task.TaskCategory.LEARNING);
        CATEGORY_KEYWORDS.put("learn", Task.TaskCategory.LEARNING);
        CATEGORY_KEYWORDS.put("course", Task.TaskCategory.LEARNING);
        CATEGORY_KEYWORDS.put("book", Task.TaskCategory.LEARNING);
        CATEGORY_KEYWORDS.put("tutorial", Task.TaskCategory.LEARNING);
        CATEGORY_KEYWORDS.put("training", Task.TaskCategory.LEARNING);
        CATEGORY_KEYWORDS.put("exam", Task.TaskCategory.LEARNING);
        
        // Shopping-related keywords
        CATEGORY_KEYWORDS.put("buy", Task.TaskCategory.SHOPPING);
        CATEGORY_KEYWORDS.put("shop", Task.TaskCategory.SHOPPING);
        CATEGORY_KEYWORDS.put("grocery", Task.TaskCategory.SHOPPING);
        CATEGORY_KEYWORDS.put("purchase", Task.TaskCategory.SHOPPING);
        
        // Finance-related keywords
        CATEGORY_KEYWORDS.put("bill", Task.TaskCategory.FINANCE);
        CATEGORY_KEYWORDS.put("payment", Task.TaskCategory.FINANCE);
        CATEGORY_KEYWORDS.put("invoice", Task.TaskCategory.FINANCE);
        CATEGORY_KEYWORDS.put("tax", Task.TaskCategory.FINANCE);
        CATEGORY_KEYWORDS.put("budget", Task.TaskCategory.FINANCE);
        CATEGORY_KEYWORDS.put("money", Task.TaskCategory.FINANCE);
        
        // Social-related keywords
        CATEGORY_KEYWORDS.put("party", Task.TaskCategory.SOCIAL);
        CATEGORY_KEYWORDS.put("event", Task.TaskCategory.SOCIAL);
        CATEGORY_KEYWORDS.put("wedding", Task.TaskCategory.SOCIAL);
        CATEGORY_KEYWORDS.put("hangout", Task.TaskCategory.SOCIAL);
    }

    // Priority keywords
    private static final Map<String, Task.TaskPriority> PRIORITY_KEYWORDS = new HashMap<>();
    
    static {
        PRIORITY_KEYWORDS.put("urgent", Task.TaskPriority.URGENT);
        PRIORITY_KEYWORDS.put("asap", Task.TaskPriority.URGENT);
        PRIORITY_KEYWORDS.put("emergency", Task.TaskPriority.URGENT);
        PRIORITY_KEYWORDS.put("important", Task.TaskPriority.HIGH);
        PRIORITY_KEYWORDS.put("critical", Task.TaskPriority.URGENT);
        PRIORITY_KEYWORDS.put("deadline", Task.TaskPriority.HIGH);
    }

    public String categorizeTask(String title, String description) {
        if (title == null || title.isBlank()) {
            return "GENERAL";
        }

        String text = (title + " " + (description != null ? description : "")).toLowerCase();
        
        for (Map.Entry<String, Task.TaskCategory> entry : CATEGORY_KEYWORDS.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue().name();
            }
        }
        
        return "GENERAL";
    }

    public String suggestPriority(String title, String description, String category) {
        if (title == null || title.isBlank()) {
            return "MEDIUM";
        }

        String text = (title + " " + (description != null ? description : "")).toLowerCase();
        
        for (Map.Entry<String, Task.TaskPriority> entry : PRIORITY_KEYWORDS.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue().name();
            }
        }
        
        // Default priority based on category
        if (category != null) {
            switch (category) {
                case "WORK":
                    return "HIGH";
                case "HEALTH":
                    return "HIGH";
                case "FINANCE":
                    return "MEDIUM";
                default:
                    return "MEDIUM";
            }
        }
        
        return "MEDIUM";
    }

    public String generateTaskSummary(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return "No tasks to summarize.";
        }

        int total = tasks.size();
        long completed = tasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.IN_PROGRESS).count();
        long pending = tasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.PENDING).count();
        
        // Find urgent/high priority tasks
        List<Task> urgentTasks = tasks.stream()
                .filter(t -> t.getPriority() == Task.TaskPriority.URGENT || t.getPriority() == Task.TaskPriority.HIGH)
                .filter(t -> t.getStatus() != Task.TaskStatus.COMPLETED)
                .limit(3)
                .collect(Collectors.toList());

        StringBuilder summary = new StringBuilder();
        summary.append(String.format("You have %d tasks: %d completed, %d in progress, and %d pending. ", 
                total, completed, inProgress, pending));
        
        if (!urgentTasks.isEmpty()) {
            summary.append("⚠️ Priority tasks: ");
            summary.append(urgentTasks.stream()
                    .map(Task::getTitle)
                    .collect(Collectors.joining(", ")));
            summary.append(".");
        }
        
        double completionRate = total > 0 ? (double) completed / total * 100 : 0;
        summary.append(String.format(" Completion rate: %.0f%%.", completionRate));
        
        return summary.toString();
    }

    public String suggestDeadline(String title, String description, String category) {
        // Suggest deadlines based on category
        if (category == null) {
            return "";
        }
        
        LocalDateTime suggestedDate;
        switch (category) {
            case "WORK":
                suggestedDate = LocalDateTime.now().plusDays(3);
                break;
            case "HEALTH":
                suggestedDate = LocalDateTime.now().plusDays(1);
                break;
            case "FINANCE":
                suggestedDate = LocalDateTime.now().plusDays(7);
                break;
            case "LEARNING":
                suggestedDate = LocalDateTime.now().plusDays(14);
                break;
            default:
                suggestedDate = LocalDateTime.now().plusDays(7);
        }
        
        return suggestedDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String generateTaskSuggestion(Task task) {
        if (task == null) {
            return "";
        }
        
        StringBuilder suggestion = new StringBuilder();
        
        // Priority-based suggestions
        if (task.getPriority() == Task.TaskPriority.URGENT) {
            suggestion.append("🚨 This is urgent! Focus on completing this task first. ");
        } else if (task.getPriority() == Task.TaskPriority.HIGH) {
            suggestion.append("⚡ This is important. Try to complete it within the next 24-48 hours. ");
        }
        
        // Category-based suggestions
        if (task.getCategory() != null) {
            switch (task.getCategory()) {
                case WORK:
                    suggestion.append("Break this into smaller subtasks for better progress. ");
                    break;
                case LEARNING:
                    suggestion.append("Set aside dedicated time blocks for focused study. ");
                    break;
                case HEALTH:
                    suggestion.append("Schedule this in your calendar as a non-negotiable appointment. ");
                    break;
                case SHOPPING:
                    suggestion.append("Consider making a list to ensure you get everything in one trip. ");
                    break;
                default:
                    break;
            }
        }
        
        // Description-based suggestions
        if (task.getDescription() != null && task.getDescription().length() > 100) {
            suggestion.append("Consider breaking this task into smaller, more manageable steps.");
        }
        
        return suggestion.toString().trim();
    }

    public String analyzeProductivity(List<Task> completedTasks, List<Task> pendingTasks) {
        int completed = completedTasks != null ? completedTasks.size() : 0;
        int pending = pendingTasks != null ? pendingTasks.size() : 0;
        int total = completed + pending;
        
        if (total == 0) {
            return "Start adding tasks to track your productivity!";
        }
        
        double completionRate = (double) completed / total * 100;
        
        StringBuilder analysis = new StringBuilder();
        
        if (completionRate >= 70) {
            analysis.append("🌟 Excellent productivity! You've completed ").append(completed).append(" out of ").append(total).append(" tasks. ");
        } else if (completionRate >= 40) {
            analysis.append("📈 Good progress! You're at ").append(String.format("%.0f%%", completionRate)).append(" completion. ");
        } else {
            analysis.append("💪 Keep going! You have ").append(pending).append(" pending tasks. ");
        }
        
        // Priority analysis
        if (pendingTasks != null && !pendingTasks.isEmpty()) {
            long urgentPending = pendingTasks.stream()
                    .filter(t -> t.getPriority() == Task.TaskPriority.URGENT)
                    .count();
            
            if (urgentPending > 0) {
                analysis.append(String.format("⚠️ You have %d urgent task(s) that need immediate attention. ", urgentPending));
            }
            
            // Suggest next action
            Task nextTask = pendingTasks.stream()
                    .filter(t -> t.getPriority() == Task.TaskPriority.URGENT || t.getPriority() == Task.TaskPriority.HIGH)
                    .findFirst()
                    .orElse(pendingTasks.get(0));
            
            if (nextTask != null) {
                analysis.append("💡 Consider tackling \"").append(nextTask.getTitle()).append("\" next.");
            }
        }
        
        return analysis.toString().trim();
    }
}
