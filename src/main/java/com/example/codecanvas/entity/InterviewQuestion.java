package com.example.codecanvas.entity;

import javax.persistence.*;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private InterviewSession session;

    private int orderIndex;

    @Column(columnDefinition = "TEXT")
    private String questionText;
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String userAnswer;

    private Double score;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String suggestedAnswer;

    public InterviewQuestion() {}

    public InterviewQuestion(InterviewSession session, int orderIndex, String questionText, String topic) {
        this.session = session;
        this.orderIndex = orderIndex;
        this.questionText = questionText;
        this.topic = topic;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public InterviewSession getSession() { return session; }
    public void setSession(InterviewSession session) { this.session = session; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }
    public String getSuggestedAnswer() { return suggestedAnswer; }
    public void setSuggestedAnswer(String suggestedAnswer) { this.suggestedAnswer = suggestedAnswer; }
}