# RingBuffer

MessageCollectors class has two statics method that provides two different implementations. Library clients should use one of those method. The difference between them is in the mechanism they rely on.
I decided to implement Ring Buffer. It seemed natural to me. I provided 2 implementations: LockBasedRingBuffer  and LockFreeRingBuffer. 
LockBasedRingBuffer uses Locks internally to achieve synchronziation. It is quite simple and straightforward. In my opinion it should just work. Locked blocks are quite small so the performance should be fine. 
I also tried hard to implement a ring buffer without locks. Some of my tries were quite bad(you can see them in commitis history). It took me quite some time and I am not sure if the final implementation is 100% correct(for example it doesn't work on a snapshot of data). 
I decided to attach it just to show that I didn't spend so much time on the LockBasedRingBuffer. 

