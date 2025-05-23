document.addEventListener('DOMContentLoaded', function() {
    const quizForm = document.getElementById('quizForm');
    if (quizForm) {
        initQuizForm(quizForm);
    }
});

function initQuizForm(form) {
    form.addEventListener('submit', function(e) {
        e.preventDefault();

        const answers = collectAnswers();
        submitAnswers(answers);
    });
}

function collectAnswers() {
    const answers = [];
    const questions = JSON.parse(document.getElementById('exercise-data').textContent).questions;

    questions.forEach(question => {
        const selectedAnswers = [];
        const inputs = document.querySelectorAll(`input[name="question_${question.id}"]:checked`);

        inputs.forEach(input => {
            selectedAnswers.push(parseInt(input.value));
        });

        answers.push({
            questionId: question.id,
            selectedAnswerIds: selectedAnswers
        });
    });

    return answers;
}

function submitAnswers(answers) {
    const exerciseId = document.getElementById('exercise-id').value;

    fetch(`/student/exercises/${exerciseId}/submit`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(answers)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(result => {
            if (result.status === 'COMPLETED') {
                showSuccess(`Тест завершен!%`);
                setTimeout(() => {
                    window.location.href = '/student/courses';
                }, 2000);
            } else {
                showError('Ошибка при проверке теста');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showError('Произошла ошибка при отправке ответов');
        });
}

function handleSubmissionResult(result) {
    if (result.status === 'COMPLETED') {
        showSuccess(`Тест завершен%`);
        setTimeout(() => {
            window.location.href = '/student/courses';
        }, 2000);
    } else {
        showError('Ошибка при проверке теста');
    }
}

function showSuccess(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success alert-dismissible fade show';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.querySelector('.card-body').appendChild(alertDiv);
}

function showError(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.querySelector('.card-body').appendChild(alertDiv);
}