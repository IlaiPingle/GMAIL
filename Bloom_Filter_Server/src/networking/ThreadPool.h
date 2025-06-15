#ifndef THREAD_POOL_H
#define THREAD_POOL_H
#include <thread>
#include <vector>
#include <queue>
#include <atomic> 
#include <mutex>
#include <condition_variable>
#include <functional>

using namespace std;
class ThreadPool {
    private:
        vector<thread> workers;
        queue<function<void()>> tasks;
        mutex queueMutex;
        condition_variable condition;
        atomic<bool> stop;
        void workerFunction();
    public:
        ThreadPool(unsigned int numThreads);
        ~ThreadPool();
        void addTask(function<void()> task);
};
#endif // THREAD_POOL_H