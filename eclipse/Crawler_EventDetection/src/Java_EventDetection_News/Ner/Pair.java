package Java_EventDetection_News.Ner;
public class Pair<T, U>
{
    public final T first;
    public final U second;

    public Pair(T first, U second)
    {
        this.first = first;
        this.second = second;
    }

    public T getFirst()
    {
        return first;
    }

    public T getKey()
    {
        return first;
    }

    public U getSecond()
    {
        return second;
    }

    public U getValue()
    {
        return second;
    }

    @Override
    public String toString()
    {
        return first + "=" + second;
    }
}