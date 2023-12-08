import functools


def viz(fn):
    """
    print function input / output
    visualize recursion via indentation
    """
    @functools.wraps(fn)
    def wrapped(*args, **kwargs):
        nonlocal count
        if count == 0:
            print()
        print(' |' * count, '=>', *args)
        count += 1
        res = fn(*args, **kwargs)
        count -= 1
        print(' |' * count, '<=', res)
        return res
    count = 0
    return wrapped
