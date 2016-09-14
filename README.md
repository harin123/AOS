# multi threaded programming
Mutual exclusion service using Raymond’s tree-based distributed mutual exclusion algorithm.
Spanning tree constructed to build the initial tree used by the Raymond’s algorithm. Service provides two function calls to the
application: cs-enter and cs-leave. The first function call cs-enter allows an application to request 
permission to start executing its critical section. The function call is blocking and returns only
when the invoking application can execute its critical section. The second function call cs-leave
allows an application to inform the service that it has finished executing its critical section.

