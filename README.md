Options completion plugin
====================

https://plugins.jetbrains.com/plugin/7822

Parses [phpDocumentor's hash description](https://github.com/phpDocumentor/fig-standards/blob/master/proposed/phpdoc.md#examples-12) and shows supported keys.

```php
class Element {
    /**
     * Initializes this class with the given options.
     *
     * @param array $options {
     *     @var bool   $required Whether this element is required
     *     @var string $label    The display name for this element
     * }
     */
    public function __construct(array $options = array())
    {
        <...>
    }
    <...>
}


new Element(['label' => 'Bob', '|' ]);
//                              | ctrl+space will show supported attributes
```
