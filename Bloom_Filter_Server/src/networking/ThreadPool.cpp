#include "ThreadPool.h"

ThreadPool::ThreadPool(unsigned int numThreads) : stop(false) {
    for (size_t i = 0; i < numThreads; ++i) {
        workers.emplace_back(&ThreadPool::workerFunction, this);
    }
}

ThreadPool::~ThreadPool() {
    {
        unique_lock<mutex> lock(queueMutex);
        stop = true;
    }
    condition.notify_all();
    for (thread &worker : workers) {
        if (worker.joinable()) {
            worker.join();
        }
    }
}

void ThreadPool::addTask(function<void()> task) {
    {
        unique_lock<mutex> lock(queueMutex);
        tasks.emplace(move(task));
    }
    condition.notify_one();
}

void ThreadPool::workerFunction() {
    function<void()> task;
    while (true){
        {
            unique_lock<mutex> lock(queueMutex);
            condition.wait(lock, [this] { return stop || !tasks.empty(); });
            if (stop && tasks.empty()) {
                return;
            }
            task = move(tasks.front());
            tasks.pop();
        }
        task();
    } 
}

