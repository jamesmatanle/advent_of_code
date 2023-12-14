import functools


def spy(x):
    print('spy:', x)
    return x


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


def print_grid(A):
    print('  ' + ' '.join(str(j % 10) for j in range(len(A[0]))))
    [print(str(i % 10), ' '.join(row)) for i, row in enumerate(A)]
