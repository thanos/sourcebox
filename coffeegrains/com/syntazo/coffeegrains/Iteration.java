package com.syntazo.coffeegrains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iteration {

	public static abstract interface Lambda {
		public Object process(final Object... args);
	}

	public static abstract class ObjectItera implements Iterable<Object> {
		public String toString() {
			return "(" + str(this) + ")";
		}
	}

	public static class Tuple {
		public Object[] values;
		public Tuple(Object... args) {
			values = args;
		}
		public String toString() {
			return "(" + str(values) + ")";
		}

	}

	public static Object[] tuple(final Object... args) {
		return args;
	}

	public static interface IteratorMaker<T> {
		public Iterator<T> iterator();
	}

	public static Iterable<Object> buildObjectIterable(
			final IteratorMaker<Object> iteratorMaker) {
		return new Iterable<Object>() {
			public Iterator<Object> iterator() {
				return iteratorMaker.iterator();
			}
			public String toString() {
				return "(" + str(this) + ")";
			}
		};
	}

	public static Iterable<Integer> buildIntegerIterable(
			final IteratorMaker<Integer> iteratorMaker) {
		return new Iterable<Integer>() {
			public Iterator<Integer> iterator() {
				return iteratorMaker.iterator();
			}
			public String toString() {
				return "(" + str(this) + ")";
			}
		};
	}

	public static Iterable iterable(final Object... elements) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					int index = 0;
					public boolean hasNext() {
						return index < elements.length;
					}

					public Object next() {
						return elements[index++];
					}

					public void remove() {
					}
				};
			}
		});
	}

	public static Iterable<Integer> irange(final int stop) {
		return irange(0, stop);
	}
	public static Iterable<Integer> irange(final int start, final int stop) {
		return irange(start, stop, 1);
	}
	public static Iterable<Integer> irange(final int start, final int stop,
			final int step) {
		return buildIntegerIterable(new IteratorMaker<Integer>() {
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int index = start;
					public boolean hasNext() {
						return index < stop;
					}
					public Integer next() {
						int i = index;
						index += step;
						return i;
					}
					public void remove() {
					}
				};
			}
		});
	}

	public static class Enum {
		final public int index;
		final public Object value;
		public Enum(int index, Object value) {
			this.index = index;
			this.value = value;
		}

		public String toString() {
			return "(" + index + "," + value + ")";
		}

	}

	public static Iterable<Enum> ienum(final Iterable<?> iterable) {
		return new Iterable<Enum>() {
			public Iterator<Enum> iterator() {
				return new Iterator<Enum>() {
					int index = 0;
					Iterator<?> iterator = iterable != null ? iterable
							.iterator() : Null;
					public boolean hasNext() {
						return iterator.hasNext();
					}

					public Enum next() {
						return new Enum(index++, iterator.next());
					}
					public void remove() {
					}
				};
			}
			public String toString() {
				return "(" + str(this) + ")";
			}
		};
	}

	public static Iterable<Object> slice(Iterable<?> iterable, int stop) {
		return slice(iterable, 0, stop);
	}
	public static Iterable<Object> slice(Iterable<?> iterable, int start,
			int stop) {
		return slice(iterable, start, stop, 1);
	}
	public static Iterable<Object> slice(final Iterable<?> iterable,
			final int start, final int stop, final int step) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					Iterator<Enum> iterator = ienum(iterable).iterator();
					Iterator<Integer> range = irange(start, stop, step)
							.iterator();
					public boolean hasNext() {
						return range.hasNext() && iterator.hasNext();
					}

					public Object next() {
						int i = range.next();
						do {
							Enum tuple = iterator.next();
							if (i == tuple.index)
								return tuple.value;
						} while (iterator.hasNext());
						throw new NoSuchElementException();
					}
					public void remove() {
					}
				};
			}
		});
	}

	public final static Iterator<Object> Null = new Iterator<Object>() {
		public boolean hasNext() {
			return false;
		}
		public Object next() {
			return null;
		}
		public void remove() {
		}
	};

	public static Iterable<Object> map(final Lambda lambda,
			final Iterable<?>... iterables) {
		final Iterator<?>[] iterators = new Iterator[iterables.length];
		for (int i = 0; i < iterables.length; i++)
			iterators[i] = iterables[i] != null
					? iterables[i].iterator()
					: Null;

		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					public boolean hasNext() {
						for (Iterator<?> iterator : iterators) {
							if (iterator.hasNext() == false)
								return false;
						}
						return true;
					}

					public Object next() {
						return lambda.process(map(iterators, new Lambda() {
							public Object process(Object... args) {
								return ((Iterator<?>) args[0]).next();
							}
						}));

					}

					public void remove() {
					}

				};
			}
		});

	}

	public static Iterable<Object> zip(final Iterable<?>... iterables) {
		final Iterator<?>[] iterators = new Iterator[iterables.length];
		for (int i = 0; i < iterables.length; i++)
			iterators[i] = iterables[i] != null
					? iterables[i].iterator()
					: Null;

		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					public boolean hasNext() {
						for (Iterator<?> iterator : iterators) {
							if (iterator.hasNext() == false)
								return false;
						}
						return true;
					}

					public Object next() {
						return iterable(map(iterators, new Lambda() {
							public Object process(Object... args) {
								return ((Iterator<?>) args[0]).next();
							}
						}));

					}

					public void remove() {
					}

				};
			}
		});

	}

	public static Iterable<Object> imap(final Lambda lambda,
			final Iterable<?>... iterables) {
		return new Iterable<Object>() {
			public Iterator<Object> iterator() {
				final Iterator<?>[] iterators = new Iterator[iterables.length];
				for (int i = 0; i < iterables.length; i++)
					iterators[i] = iterables[i] != null ? iterables[i]
							.iterator() : Null;
				return new Iterator<Object>() {
					public boolean hasNext() {
						for (Iterator<?> iterator : iterators) {
							if (iterator.hasNext() == false)
								return false;
						}
						return true;
					}

					public Object next() {
						return lambda.process(map(iterators, new Lambda() {
							public Object process(Object... args) {
								return ((Iterator<?>) args[0]).next();
							}
						}));

					}

					public void remove() {
					}
				};
			}

		};

	}

	public static Iterable<Object> ifilter(final Lambda lambda,
			final Iterable<?> iterable) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					Iterator<?> iterator = iterable != null ? iterable
							.iterator() : Null;
					Object nextObj = null;

					public boolean hasNext() {
						nextObj = getNext(nextObj);
						return nextObj != null;
					}

					public Object getNext(Object obj) {
						while (obj == null && iterator.hasNext()) {
							obj = lambda.process(iterator.next());
						}
						return obj;
					}

					public void remove() {
						iterator.remove();
					}

					public Object next() {
						Object obj = getNext(nextObj);
						nextObj = null;
						return obj;
					}
				};
			}
		});
	}

	
	
	public static Iterable<Object> takewhile(final Lambda lambda,
			final Iterable<?> iterable) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					Iterator<?> iterator = iterable != null ? iterable
							.iterator() : Null;
					Object nextObj = null;

					public boolean hasNext() {
						nextObj = getNext(nextObj);
						return nextObj != null;
					}

					public Object getNext(Object obj) {
						if  (obj == null && iterator.hasNext()) {
							obj = lambda.process(iterator.next());
						}
						return obj;
					}

					public void remove() {
						iterator.remove();
					}

					public Object next() {
						Object obj = getNext(nextObj);
						nextObj = null;
						return obj;
					}
				};
			}
		});
	}

	public static Iterable<Object> dropwhile(final Lambda lambda,
			final Iterable<?> iterable) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					Iterator<?> iterator = iterable != null ? iterable
							.iterator() : Null;
					boolean dropZone = true;
					public boolean hasNext() {
							return iterator.hasNext();
					}

					public void remove() {
						iterator.remove();
					}

					public Object next() {
						Object obj = iterator.next();
						while (dropZone &&  iterator.hasNext()) {
							dropZone = (Boolean)lambda.process(obj);
							if (dropZone) obj = iterator.next();
						}
						dropZone = false;
						return obj;
					}
				};
			}
		});
	}

	
	
	public static Object reduce(Lambda lambda, final Iterable<Object> iterable,
			Object start) {
		Object result = start;
		for (Object obj : iterable) {
			result = lambda.process(result, obj);
		}
		return result;
	}

	/*
	 * Make an iterator that returns elements from the first iterable until it
	 * is exhausted, then proceeds to the next iterable, until all of the
	 * iterables are exhausted. Used for treating consecutive sequences as a
	 * single sequence.
	 */
	public static Iterable<Object> chain(final Iterable<?>... iterables) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					int iterableIndex = 0;
					Iterator<?> iterator = iterables[0] != null ? iterables[0]
							.iterator() : Null;

					public boolean hasNext() {
						boolean more = iterator.hasNext();
						while (more == false
								&& iterableIndex < iterables.length - 1) {
							iterator = iterables[iterableIndex++] == null
									? Null
									: iterables[iterableIndex].iterator();
							more = iterator.hasNext();
						}
						return more;
					}

					public Object next() {
						hasNext();
						return iterator.next();
					}

					public void remove() {
						iterator.remove();
					}
				};
			}
		});
	}

	public static Iterable<Object> iterate(final int count,
			final Iterable<?> iterable) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					int number = 0;
					Iterator<?> iterator = iterable != null ? iterable
							.iterator() : Null;
					public boolean hasNext() {
						return number < count && iterator.hasNext();
					}
					public Object next() {
						number++;
						return iterator.next();
					}
					public void remove() {
						iterator.remove();
					}
				};
			}
		});
	}

	/*
	 * Make an Iterable that returns consecutive integers starting with n.
	 */

	public static Iterable<Integer> count(final int num) {
		return buildIntegerIterable(new IteratorMaker<Integer>() {
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int iterableIndex = num;
					public boolean hasNext() {
						return true;
					}

					public Integer next() {
						return iterableIndex++;
					}
					public void remove() {
					}
				};
			}
		});
	}

	/*
	 * Make an iterator endlessly returning elements from the iterable.
	 */

	public static Iterable<Object> cycle(final Iterable<?> iterable) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					Iterator<?> iterator = iterable != null ? iterable
							.iterator() : Null;
					public boolean hasNext() {
						return true;
					}

					public Object next() {
						if (iterator.hasNext() == false)
							iterator = iterable.iterator();
						return iterator.next();
					}

					public void remove() {
						iterator.remove();
					}

				};
			}
		});
	}

	
	public static Iterable<Object> repeat(final Object object) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					public boolean hasNext() {
						return true;
					}
					public Object next() {
						return object;
					}
					public void remove() {}
				};
			}
		});
	}
	
	
	public static Iterable<Object> repeat(int count, Object... objects) {

		return repeat(count, iterable(objects));
	}

	public static Iterable<Object> repeat(final int count,
			final Iterable<?> iterable) {
		return buildObjectIterable(new IteratorMaker<Object>() {
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {
					Iterator<?> iterator = iterable != null ? iterable
							.iterator() : Null;
					int cycleNo = 0;
					public boolean hasNext() {
						if (iterator.hasNext() == false) {
							iterator = iterable.iterator();
							cycleNo++;
						}
						return cycleNo < count;
					}

					public Object next() {
						return iterator.next();
					}

					public void remove() {
						iterator.remove();
					}

				};
			}
		});
	}
	public static boolean all(Lambda lambda, Iterable<?> iterable) {
		for (Object obj : iterable) {
			if (lambda.process(obj) == null)
				return false;
		}
		return true;
	}
	public static boolean any(Lambda lambda, Iterable<?> iterable) {
		for (Object obj : iterable) {
			if (lambda.process(obj) == null)
				return true;
		}
		return false;
	}

	
	
	
	
	
	
	public static Object[] map(Object[] array, Lambda lambda) {
		return map(array, lambda, 0, array.length);
	}
	public static Object[] map(Object[] array, Lambda lambda, int start,
			int stop) {
		Object[] result = new Object[stop - start];
		for (int i = start; i < stop; i++)
			result[i] = lambda.process(array[i]);
		return result;
	}
	
	
	
	
	
	
	
	

	// public static Object[] filter(Object[]array, Lambda lambda) throws
	// Exception {
	// Collection<Object> result = new ArrayList<Object>();
	// for (Object obj : array) {
	// Object value = lambda.process(obj);
	// if (value != null)
	// result.add(value);
	// }
	// return result.toArray();
	// }
	//
	//
	//
	//
	// public static Object reduce(Object[]array, Lambda lambda) throws
	// Exception {
	// Object result = null;
	// for (Object obj : array) {
	// result = lambda.process(result, obj);
	// }
	// return result;
	// }
	// 
	//
	// public static boolean all(Object[]array, Lambda lambda) throws Exception
	// {
	// for (Object obj : array) {
	// if ((Boolean)lambda.process(obj) == false)
	// return false;
	// }
	// return true;
	// }
	public static boolean any(Object[] array, Lambda lambda) throws Exception {
		for (Object obj : array) {
			if ((Boolean) lambda.process(obj) == false)
				return true;
		}
		return false;
	}
	public static String str(Iterable<?> parts) {
		return join(parts, ",");
	}
	public static String str(Object[] parts) {
		return join(parts, ",");
	}
	public static String join(Iterable<?> parts, String del) {

		StringBuffer result = new StringBuffer();
		int i = 0;
		for (Object part : parts) {
			result.append(part);
			result.append(del);
			i++;
		}
		return i > 0
				? result.substring(0, result.length() - del.length())
				: result.toString();
	}

	public static String join(Object[] parts, String del) {

		StringBuffer result = new StringBuffer();
		int i = 0;
		for (Object part : parts) {
			result.append(part);
			result.append(del);
			i++;
		}
		return i > 0
				? result.substring(0, result.length() - del.length())
				: result.toString();
	}

	public static Collection<Object> toCollection(Collection<Object> container,
			Object... elements) {
		for (Object obj : elements)
			container.add(obj);
		return container;
	}

	public static void main(String[] args) {

		Collection<Object> a = toCollection(new ArrayList<Object>(), 1, 2, 3,
				4, 5, 6, 3, 3, 10);
		System.out
				.println("Collection<Object> a = toCollection(new ArrayList<Object>(), 1,2,3,4,5,6,3,3,10): "
						+ str(a));
		Iterable<Integer> b = irange(2, 10, 3);
		System.out.println("Iterable<Integer> b = irange(2,10,3): " + b);
		Iterable<Object> c = iterable(1, 2, 3, 4);
		System.out.println("Iterable c = iterable(1,2,3): " + c);

		System.out.println("testing ienum(b): " + ienum(c));

		Iterator forever = repeat(4).iterator();
		System.out.println("Iterable forever = repeat(4).iterator()");
		System.out.println("testing forever.next(), forever.next(): "
				+ forever.next()+","+forever.next());
		
		System.out.println("testing repeat(3, iterable(1,2,3)): "
				+ repeat(3, iterable(1, 2, 3)));

		System.out.println("testing repeat(3, iterable(iterable(1,2,3))): "
				+ repeat(3, iterable(iterable(1, 2, 3))));
		System.out.println("testing map(lambda x: x*10, b): "
				+ map(new Lambda() {
					public Object process(Object... args) {
						return ((Integer) args[0]) * 10;
					}
				}, b));

		System.out.println("testing map(lambda x,y: x*10+y, a, b): "
				+ map(new Lambda() {
					public Object process(Object... args) {
						return ((Integer) args[0]) * 10 + ((Integer) args[1]);
					}
				}, a, b));

		System.out.println("testing filter(lambda x: x%2==0, a): "
				+ ifilter(new Lambda() {
					public Object process(Object... args) {
						return ((Integer) args[0]) % 2 == 0 ? args[0] : null;
					}
				}, a));

		System.out.println("testing reduce(lambda x,y: x+y, a,0): "
				+ reduce(new Lambda() {
					public Object process(Object... args) {
						return ((Integer) args[0]) + ((Integer) args[1]);
					}
				}, a, 0));

		System.out.println("testing slice(" + str(a) + "\n\t, 1,10,3): "
				+ slice(a, 1, 10, 3));
		System.out.println("testing chain(a,b): " + chain(a, b));
		System.out.println("testing chain(a,b,a): " + chain(a, b, a));

		System.out.println("testing iterate(10, count(20)): "
				+ iterate(10, count(20)));

		System.out.println("testing iterate(10, cycle(b)): "
				+ iterate(10, cycle(b)));

		System.out.println("testing all(lambda x: x==2, " + b + ": "
				+ all(new Lambda() {
					public Object process(Object... args) {
						return (Integer) args[0] == 2;
					}
				}, b));

		System.out.println("testing any(lambda x: x==2, " + b + ": "
				+ any(new Lambda() {
					public Object process(Object... args) {
						return (Integer) args[0] == 2;
					}
				}, b));

		System.out
				.println("testing any(lambda x: x==2, repeat(10, iterable(1)): "
						+ any(new Lambda() {
							public Object process(Object... args) {
								return (Integer) args[0] == 2;
							}
						}, repeat(10, iterable(1))));

		System.out
				.println("testing all(lambda x: x==1, repeat(10, iterable(1)): "
						+ all(new Lambda() {
							public Object process(Object... args) {
								return (Integer) args[0] == 2;
							}
						}, repeat(10, iterable(1))));

		System.out.println("testing zip(a,b,c): " + zip(a, b, c));
		
		System.out.println("testing takewhile(lambda x: x< 4, a): "
				+ takewhile(new Lambda() {
					public Object process(Object... args) {
						return (Integer) args[0] < 4 ? args[0]: null;
					}
				}, a));
		
		System.out.println("testing dropwhile(lambda x: x< 4, a): "
				+ dropwhile(new Lambda() {
					public Object process(Object... args) {
						return (Integer) args[0] < 4;
					}
				}, a));
		
		System.out.println("example enumerate = zip(count().iterator(), a): "
				+ zip(count(0), a));
		
		System.out.println("example dot product of (1,2,3,4) * (4,5,6,7): "+reduce(new Lambda() {
			public Object process(Object... args) {
				return (Integer)args[0]+(Integer)args[1];
			}}, map(new Lambda () {

			public Object process(Object... args) {
				return (Integer)args[0]*(Integer)args[1];
			}}, iterable(1, 2, 3, 4),iterable(4, 5, 6, 7)),0));
	}
	
	

}
